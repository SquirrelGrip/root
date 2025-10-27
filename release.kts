#!/usr/bin/env kotlin
@file:Suppress("SameParameterValue")

import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64
import kotlin.system.exitProcess

val dryRun: Boolean = false
val debug: Boolean = false

// ---- Helpers ----
fun section(title: String) {
    println("\n==> $title")
}

fun need(cmd: String) {
    // For ./mvnw ensure file exists and is executable; otherwise rely on PATH
    if (cmd.startsWith("./")) {
        val f = File(cmd)
        if (!(f.exists() && f.canExecute())) {
            System.err.println("ERROR: Required command '$cmd' not found or not executable")
            exitProcess(1)
        }
        return
    }
    val which = runCatching { run(listOf("which", cmd), check = false, quiet = true) }.getOrNull()
    if (which == null || which.exitCode != 0) {
        System.err.println("ERROR: Required command '$cmd' not found in PATH")
        exitProcess(1)
    }
}

data class RunResult(val exitCode: Int, val stdout: String)

fun run(cmd: List<String>, check: Boolean = true, quiet: Boolean = false): RunResult {
    if (debug) {
        println("> ${cmd.joinToString(" ")}")
    }
    val pb = ProcessBuilder(cmd)
    pb.redirectErrorStream(true)
    val proc = pb.start()

    val sb = StringBuilder()
    proc.inputStream.bufferedReader().use {
        while (true) {
            val line = it.readLine() ?: break
            sb.appendLine(line)
            if (!quiet) println(line)
        }
    }
    val code = proc.waitFor()
    if (debug) {
        println("< $code")
    }
    if (check && code != 0) {
        System.err.println("ERROR: command failed (${cmd.joinToString(" ")}) with code $code")
        exitProcess(code)
    }
    return RunResult(code, sb.toString())
}

fun httpGet(url: String, headers: Map<String, String> = emptyMap()): Pair<Int, String> {
    if (debug) {
        println("> GET $url")
        headers.forEach { (k, v) -> println("> $k: $v") }
    }
    val conn = (URI.create(url).toURL().openConnection() as HttpURLConnection)
    headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
    conn.connectTimeout = 15000
    conn.readTimeout = 30000
    conn.requestMethod = "GET"
    return try {
        val code = conn.responseCode
        if (debug) {
            println("< $code ${conn.responseMessage}")
        }
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val body = (stream ?: conn.inputStream)?.bufferedReader()?.use { it.readText() } ?: ""
        if (debug) {
            println("< $body")
        }
        Pair(code, body)
    } catch (e: Exception) {
        System.err.println("ERROR: HTTP request failed for $url: ${e.message}")
        Pair(0, "")
    } finally {
        conn.disconnect()
    }
}

fun resolveVersion(): String {
    section("Resolving project version")
    val res = run(listOf("./mvnw", "-q", "-Dexec.skip=true", "help:evaluate", "-Dexpression=project.version", "-DforceStdout"), check = true, quiet = true)
    var v = res.stdout.trim()
    if (v.endsWith("-SNAPSHOT")) v = v.removeSuffix("-SNAPSHOT")
    println("version=$v")
    if (v.isBlank()) {
        System.err.println("ERROR: Could not resolve project.version")
        exitProcess(1)
    }
    return v
}

fun resolveProjectName(): String {
    section("Resolving project name")
    val res = run(listOf("./mvnw", "-q", "-Dexec.skip=true", "help:evaluate", "-Dexpression=project.artifactId", "-DforceStdout"), check = true, quiet = true)
    val name = res.stdout.trim()
    println("name=$name")
    if (name.isBlank()) {
        System.err.println("ERROR: Could not resolve project.name")
        exitProcess(1)
    }
    return name
}

fun verifyGpg() {
    section("Verifying GPG signing setup by building and signing")
    val cmd = listOf(
        "./mvnw", "--batch-mode", "-s", "settings.xml", "-U",
        "package", "gpg:sign",
        "-Dgpg.keyEnvName=GPG_KEYNAME",
        "-Dgpg.passphraseEnvName=GPG_PASSPHRASE",
        "-DskipTests"
    )
    val res = run(cmd, check = false, quiet = true)
    if (res.exitCode != 0) {
        if (res.stdout.isNotBlank()) System.err.print(res.stdout)
        System.err.println("ERROR: GPG signing failed. Ensure GPG_KEYNAME and GPG_PASSPHRASE env vars are set and key is available.")
        exitProcess(1)
    }
}

fun githubAuthCheck() {
    section("Checking GitHub token authentication")
    val token = System.getenv("GIT_TOKEN")
    if (token.isNullOrBlank()) {
        println("WARNING: No GitHub token found (expected GIT_TOKEN). Skipping GitHub auth check.")
        return
    }
    val (code, body) = httpGet("https://api.github.com/user", mapOf("Authorization" to "Bearer $token"))
    if (code == 200) {
        // A tiny JSON parse just for login
        val login = Regex("\"login\"\\s*:\\s*\"([^\"]+)\"").find(body)?.groupValues?.getOrNull(1)
        if (!login.isNullOrBlank()) {
            println("Authenticated to GitHub as: $login")
            return
        }
    }
    System.err.println("ERROR: GitHub authentication failed. Check your token.")
    exitProcess(1)
}

fun ossrhBasicToken(): String {
    section("Generating OSSRH credentials")
    val name = System.getenv("OSSRH_TOKEN_NAME")
    val pwd = System.getenv("OSSRH_TOKEN_PASSWORD")
    if (name.isNullOrBlank()) {
        System.err.println("ERROR: OSSRH_TOKEN_NAME is not specified")
        exitProcess(1)
    }
    if (pwd.isNullOrBlank()) {
        System.err.println("ERROR: OSSRH_TOKEN_PASSWORD is not specified")
        exitProcess(1)
    }
    val raw = "$name:$pwd".toByteArray(StandardCharsets.UTF_8)
    return Base64.getEncoder().encodeToString(raw)
}

fun spotlessCheck() {
    section("Checking spotless")
    val res = run(listOf("./mvnw", "spotless:check"), check = false, quiet = true)
    if (res.exitCode != 0) {
        if (res.stdout.isNotBlank()) System.err.print(res.stdout)
        System.err.println("ERROR: Spotless check failed. Run './mvnw spotless:apply' to fix.")
        exitProcess(1)
    }
}

fun isPublished(name: String, version: String): Boolean {
    val url = buildString {
        append("https://central.sonatype.com/api/v1/publisher/published")
        append("?namespace=com.github.squirrelgrip")
        append("&name=")
        append(URLEncoder.encode(name, "UTF-8"))
        append("&version=")
        append(URLEncoder.encode(version, "UTF-8"))
    }
    val (code, body) = httpGet(url, mapOf(
        "accept" to "application/json",
        "Authorization" to "Basic ${ossrhBasicToken()}"
    ))
    val published = if (code == 200) {
        // naive parse of 'published': true/false
        Regex("\"published\"\\s*:\\s*(true|false)").find(body)?.groupValues?.getOrNull(1)?.toBoolean() ?: false
    } else false
    println("published=$published")
    return published
}

fun jgitflowReleaseStart() {
    section("Starting release via jgitflow")
    val res = run(listOf("./mvnw", "--batch-mode", "-s", "settings.xml", "-U", "clean", "jgitflow:release-start", "-PjgitflowStart"), check = false)
    if (res.exitCode != 0) {
        System.err.println("Build failed to prepare for release")
        exitProcess(1)
    }
}

fun jgitflowReleaseFinish() {
    section("Finishing release via jgitflow")
    val res = run(listOf("./mvnw", "--batch-mode", "-s", "settings.xml", "-U", "jgitflow:release-finish"), check = false)
    if (res.exitCode != 0) {
        System.err.println("Build failed to complete release")
        exitProcess(1)
    }
}

fun postReleaseRepoChecks(version: String) {
    section("Post-release repo checks")
    checkTagCreated(version)
    checkReleaseBranchRemoved(version)
}

fun checkTagCreated(version: String) {
    val cmd = listOf(
        "git", "ls-remote", "--tags", "origin", version
    )
    val res = run(cmd, check = false, quiet = true)
    if (res.stdout.isBlank()) {
        System.err.println("ERROR: Tag refs/tags/$version not created.")
        exitProcess(1)
    }
}

fun checkReleaseBranchRemoved(version: String) {
    val cmd = listOf(
        "git", "rev-parse", "--verify", "release/$version"
    )
    val res = run(cmd, check = false, quiet = true)
    if (res.exitCode != 0) {
        if (res.stdout.isNotBlank()) System.err.print(res.stdout)
        System.err.println("ERROR: Branch release/$version not removed.")
        exitProcess(1)
    }
}

fun mainFlow() {
    // Basic requirements
    need("./mvnw")
    need("git")
    need("gpg")

    val version = resolveVersion()
    val name = resolveProjectName()
    githubAuthCheck()
    spotlessCheck()
    verifyGpg()

    section("Checking if version $version is already published on Central")
    if (isPublished(name, version)) {
        System.err.println("Artifact already published. You need to update the version using './mvnw versions:set'")
        exitProcess(1)
    }

    // Run the release
    if (!dryRun) {
        jgitflowReleaseStart()
        jgitflowReleaseFinish()

        section("Re-checking publication status for version $version")
        if (!isPublished(name, version)) {
            System.err.println("Artifact was not published.")
            exitProcess(1)
        }

        postReleaseRepoChecks(version)
    } else {
        println("Dry Run Enabled.")
    }
}

try {
    mainFlow()
} catch (_: InterruptedException) {
    System.err.println("Aborted by user.")
    exitProcess(130)
}

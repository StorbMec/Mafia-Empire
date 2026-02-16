package dev.gangster.api

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

fun Route.fileRoutes() {
    staticFiles("/game", File("static/game"))
    staticFiles("crossdomain.xml", File("static"))
    staticFiles("/gangster-account", File("static/gangster-account"))
    staticFiles("/gangster-data", File("static/gangster-data"))
    staticFiles("/gangster-content", File("static/gangster-content"))
}

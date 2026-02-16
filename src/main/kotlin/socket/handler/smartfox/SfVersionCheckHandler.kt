package dev.gangster.socket.handler.smartfox

import dev.gangster.SOCKET_SERVER_PORT
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.SfConstants
import dev.gangster.socket.message.SfMessage
import dev.gangster.socket.protocol.SmartFoxXML

const val POLICY_RESPONSE =
    "<cross-domain-policy><allow-access-from domain='*' to-ports='$SOCKET_SERVER_PORT' /></cross-domain-policy>\u0000"

/**
 * Version check, follows original SmartFox server.
 */
class SfVersionCheckHandler : MessageHandler<SfMessage> {
    override val priority: Int
        get() = 0

    override fun match(message: SfMessage): Boolean {
        return message.type == SfConstants.TYPE_SYS && message.action == SfConstants.ACTION_VER_CHK
    }

    override suspend fun handle(
        connection: Connection,
        message: SfMessage
    ) {
        connection.sendRaw(SmartFoxXML.apiOK())
    }
}

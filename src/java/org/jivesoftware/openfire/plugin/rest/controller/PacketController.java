/*
 * Copyright (c) 2022.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.openfire.plugin.rest.controller;

import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.plugin.rest.entity.MessageEntity;
import org.jivesoftware.openfire.plugin.rest.entity.SessionEntities;
import org.jivesoftware.openfire.plugin.rest.entity.SessionEntity;
import org.jivesoftware.openfire.plugin.rest.exceptions.ExceptionType;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Presence;

import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * The Class PacketController.
 */
public class PacketController {
    /** The Constant INSTANCE. */
    public static final PacketController INSTANCE = new PacketController();
    private final ComponentManager componentManager = ComponentManagerFactory.getComponentManager();

    /**
     * Gets the single instance of PacketController.
     *
     * @return single instance of PacketController
     */
    public static PacketController getInstance() {
        return INSTANCE;
    }


    public void sendPacketToUser(String fromJID, String toJID, String content) throws ServiceException {
        if (content == null || content.isEmpty()) {
            throw new ServiceException("Message content/body is null or empty", "",
                ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
                Response.Status.BAD_REQUEST);
        }

        Message msg = new Message();
        msg.setFrom(fromJID);
        msg.setTo(toJID);
        msg.setBody(content);


        SessionManager.getInstance().userBroadcast(toJID.split("@")[0], msg);

    }

    public void broadcastPacket(String fromJID, String content) throws ServiceException {
        if (content == null || content.isEmpty()) {
            throw new ServiceException("Message content/body is null or empty", "",
                ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
                Response.Status.BAD_REQUEST);
        }

        for (ClientSession session : SessionManager.getInstance().getSessions()) {
            Message msg = new Message();
            msg.setFrom(fromJID);
            msg.setTo(session.getAddress().toString());
            msg.setBody(content);

            SessionManager.getInstance().userBroadcast(fromJID.split("@")[0], msg);
        }
    }

    public void sendPresence(String sender, String target) throws ServiceException {
        Collection<ClientSession> clientSessions = SessionManager.getInstance().getSessions(sender);
        JID to = XMPPServer.getInstance().createJID(target, null);

        for (ClientSession session : clientSessions) {
            Presence presence = new Presence();
            presence.setFrom(sender);
            presence.setTo(to);
            presence.setType(session.getPresence().getType());
            presence.setStatus(session.getPresence().getStatus());
            presence.setShow(session.getPresence().getShow());


            SessionManager.getInstance().userBroadcast(sender.split("@")[0], presence);
        }
    }
}

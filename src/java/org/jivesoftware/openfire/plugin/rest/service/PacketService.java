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

package org.jivesoftware.openfire.plugin.rest.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jivesoftware.openfire.plugin.rest.controller.PacketController;
import org.jivesoftware.openfire.plugin.rest.entity.MessageEntity;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.xmpp.packet.Presence;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("restapi/v1/packets")
@Tag(name = "Packet", description = "Sending raw XML messages to users.")
public class PacketService {

    private PacketController packetController;

    @PostConstruct
    public void init() {
        packetController = PacketController.getInstance();
    }

    @POST
    @Path("/users")
    @Operation(
        summary = "Packets",
        description = "Broadcast a packet to all users that are currently online.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Message is sent."),
            @ApiResponse(responseCode = "400", description = "The message content is empty or missing."),
            @ApiResponse(responseCode = "400", description = "The message recipient is empty or invalid."),
        }
    )
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response sendPacket(
        @RequestBody(description = "The message that is to be sent.", required = true) MessageEntity messageEntity,

        @Parameter(description = "The (bare) JID of the message sender.", example = "alex@example.org", required = true)
        @QueryParam("sender") String sender

    ) throws ServiceException {
        packetController.broadcastPacket(sender, messageEntity.getBody());
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/user/{user}")
    @Operation(
        summary = "Packets",
        description = "Send a packet to a single user.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Message is sent."),
            @ApiResponse(responseCode = "400", description = "The message content is empty or missing."),
            @ApiResponse(responseCode = "400", description = "The message recipient is empty or invalid."),
        }
    )
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response sendPacket(
        @RequestBody(description = "The message that is to be sent.", required = true) MessageEntity messageEntity,

        @Parameter(description = "The (bare) JID of the message receiver.", example = "john@example.org", required = true)
        @PathParam("user") String targetUser,

        @Parameter(description = "The (bare) JID of the message sender.", example = "alex@example.org", required = true)
        @QueryParam("sender") String sender

    ) throws ServiceException {
        packetController.sendPacketToUser(sender, targetUser, messageEntity.getBody());
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/presence/{user}/send/{to}")
    @Operation(
        summary = "Packets",
        description = "Send a packet to a single user.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Message is sent."),
            @ApiResponse(responseCode = "400", description = "The message content is empty or missing."),
            @ApiResponse(responseCode = "400", description = "The message recipient is empty or invalid."),
        }
    )
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response sendPresence(
        @Parameter(description = "The username of the presence sender.", example = "", required = true)
        @PathParam("user") String targetUser,

        @Parameter(description = "The user of the presence receiver.", example = "alex", required = true)
        @PathParam("to") String sender

    ) throws ServiceException {
        packetController.sendPresence(sender, targetUser);

        return Response.status(Response.Status.CREATED).build();
    }
}

/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.k3po.driver.internal.behavior.visitor;

import java.util.List;

import org.kaazing.k3po.driver.internal.behavior.visitor.InjectFlushVisitor.State;
import org.kaazing.k3po.lang.internal.ast.AstAcceptNode;
import org.kaazing.k3po.lang.internal.ast.AstAcceptableNode;
import org.kaazing.k3po.lang.internal.ast.AstBoundNode;
import org.kaazing.k3po.lang.internal.ast.AstChildClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstChildOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectNode;
import org.kaazing.k3po.lang.internal.ast.AstConnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectNode;
import org.kaazing.k3po.lang.internal.ast.AstDisconnectedNode;
import org.kaazing.k3po.lang.internal.ast.AstNode;
import org.kaazing.k3po.lang.internal.ast.AstOpenedNode;
import org.kaazing.k3po.lang.internal.ast.AstPropertyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstReadNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamableNode;
import org.kaazing.k3po.lang.internal.ast.AstUnbindNode;
import org.kaazing.k3po.lang.internal.ast.AstUnboundNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAwaitNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteNotifyNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteValueNode;

public class InjectFlushVisitor implements AstNode.Visitor<AstScriptNode, State> {

    public enum ReadWriteState {
        NONE, CONFIG_ONLY, CONFIG_OR_VALUE
    }

    public static final class State {
        private List<AstStreamNode> streams;
        private List<AstStreamableNode> streamables;
        private ReadWriteState readState;
        private ReadWriteState writeState;
    }

    @Override
    public AstScriptNode visit(AstScriptNode script, State state) throws Exception {

        AstScriptNode newScript = new AstScriptNode();
        newScript.setRegionInfo(script.getRegionInfo());
        newScript.getProperties().addAll(script.getProperties());

        state.streams = newScript.getStreams();

        for (AstStreamNode stream : script.getStreams()) {
            stream.accept(this, state);
        }

        return newScript;
    }

    @Override
    public AstScriptNode visit(AstPropertyNode propertyNode, State state) throws Exception {
        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptNode acceptNode, State state) throws Exception {

        state.readState = ReadWriteState.NONE;
        state.writeState = ReadWriteState.NONE;

        AstAcceptNode newAcceptNode = new AstAcceptNode(acceptNode);

        state.streamables = newAcceptNode.getStreamables();
        for (AstStreamableNode streamable : acceptNode.getStreamables()) {
            streamable.accept(this, state);
        }

        for (AstAcceptableNode acceptableNode : acceptNode.getAcceptables()) {
            acceptableNode.accept(this, state);
        }

        state.streams.add(newAcceptNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstAcceptableNode acceptableNode, State state) throws Exception {

        state.readState = ReadWriteState.NONE;
        state.writeState = ReadWriteState.NONE;

        AstAcceptableNode newAcceptableNode = new AstAcceptableNode();
        newAcceptableNode.setRegionInfo(acceptableNode.getRegionInfo());
        newAcceptableNode.setAcceptName(acceptableNode.getAcceptName());

        state.streamables = newAcceptableNode.getStreamables();
        for (AstStreamableNode streamable : acceptableNode.getStreamables()) {
            streamable.accept(this, state);
        }

        state.streams.add(newAcceptableNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectNode connectNode, State state) throws Exception {

        state.readState = ReadWriteState.NONE;
        state.writeState = ReadWriteState.NONE;

        AstConnectNode newConnectNode = new AstConnectNode(connectNode);

        state.streamables = newConnectNode.getStreamables();
        for (AstStreamableNode streamable : connectNode.getStreamables()) {
            streamable.accept(this, state);
        }

        state.streams.add(newConnectNode);

        return null;
    }

    @Override
    public AstScriptNode visit(AstReadAwaitNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteAwaitNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadNotifyNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteNotifyNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteValueNode node, State state) throws Exception {

        state.streamables.add(node);
        state.writeState = ReadWriteState.CONFIG_OR_VALUE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnbindNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstCloseNode node, State state) throws Exception {

        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildOpenedNode childOpenedNode, State state) throws Exception {
        state.streamables.add(childOpenedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstChildClosedNode childClosedNode, State state) throws Exception {

        state.streamables.add(childClosedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstOpenedNode openedNode, State state) throws Exception {

        state.streamables.add(openedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstBoundNode boundNode, State state) throws Exception {

        state.streamables.add(boundNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstConnectedNode connectedNode, State state) throws Exception {

        state.streamables.add(connectedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadValueNode node, State state) throws Exception {
        switch (state.writeState) {
        case CONFIG_ONLY:
            AstWriteFlushNode flush = new AstWriteFlushNode();
            flush.setRegionInfo(node.getRegionInfo());
            visit(flush, state);
            break;
        default:
            break;
        }

        state.streamables.add(node);
        state.readState = ReadWriteState.CONFIG_OR_VALUE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstDisconnectedNode disconnectedNode, State state) throws Exception {

        state.streamables.add(disconnectedNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstUnboundNode unboundNode, State state) throws Exception {

        state.streamables.add(unboundNode);
        return null;
    }

    @Override
    public AstScriptNode visit(AstClosedNode closedNode, State state) throws Exception {

        state.streamables.add(closedNode);
        state.readState = ReadWriteState.NONE;
        state.writeState = ReadWriteState.NONE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadConfigNode node, State state) throws Exception {
        switch (state.writeState) {
        case CONFIG_ONLY:
            AstWriteFlushNode flush = new AstWriteFlushNode();
            flush.setRegionInfo(node.getRegionInfo());
            visit(flush, state);
            break;
        default:
            break;
        }

        state.streamables.add(node);
        switch (state.readState) {
        case CONFIG_ONLY:
        case CONFIG_OR_VALUE:
            break;
        default:
            state.readState = ReadWriteState.CONFIG_ONLY;
            break;
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteConfigNode node, State state) throws Exception {
        state.streamables.add(node);
        switch (state.writeState) {
        case CONFIG_ONLY:
        case CONFIG_OR_VALUE:
            break;
        default:
            state.writeState = ReadWriteState.CONFIG_ONLY;
            break;
        }
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadClosedNode node, State state) throws Exception {
        state.streamables.add(node);
        state.readState = ReadWriteState.NONE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteCloseNode node, State state) throws Exception {
        state.streamables.add(node);
        state.writeState = ReadWriteState.NONE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteFlushNode node, State state) throws Exception {
        state.streamables.add(node);
        state.writeState = ReadWriteState.CONFIG_OR_VALUE;
        return null;
    }

    @Override
    public AstScriptNode visit(AstReadOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }

    @Override
    public AstScriptNode visit(AstWriteOptionNode node, State state) throws Exception {
        state.streamables.add(node);
        return null;
    }
}

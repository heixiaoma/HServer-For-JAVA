package top.hserver.core.server.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.rtsp.*;
import io.netty.util.CharsetUtil;

/**
 * @author hxm
 */
public class RtspServerHandler extends SimpleChannelInboundHandler<DefaultHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultHttpRequest req) throws Exception {

        FullHttpResponse rep = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,  RtspResponseStatuses.NOT_FOUND);
        if (req.method() == RtspMethods.OPTIONS)
        {
            rep.setStatus(RtspResponseStatuses.OK);
            rep.headers().add(RtspHeaderValues.PUBLIC, "DESCRIBE, SETUP, PLAY, TEARDOWN");
            sendAnswer(ctx, req, rep);
        }
        else if (req.method() == RtspMethods.DESCRIBE)
        {

            ByteBuf buf = Unpooled.copiedBuffer("c=IN IP4 10.5.110.117\r\nm=video 5004 RTP/AVP 96\r\na=rtpmap:96 H264/90000\r\n", CharsetUtil.UTF_8);
            rep.setStatus(RtspResponseStatuses.OK);
            rep.headers().add(RtspHeaderNames.CONTENT_TYPE, "application/sdp");
            rep.headers().add(RtspHeaderNames.CONTENT_LENGTH, buf.writerIndex());
            rep.content().writeBytes(buf);
            sendAnswer(ctx, req, rep);
        }
        else if (req.method() == RtspMethods.SETUP)
        {
            rep.setStatus(RtspResponseStatuses.OK);
            String session = String.format("%08x",(int)(Math.random()*65536));
            rep.headers().add(RtspHeaderNames.SESSION, session);
            rep.headers().add(RtspHeaderNames.TRANSPORT,"RTP/AVP;unicast;client_port=5004-5005");
            sendAnswer(ctx, req, rep);
        }
        else if (req.method() == RtspMethods.PLAY)
        {
            rep.setStatus(RtspResponseStatuses.OK);
            sendAnswer(ctx, req, rep);
        }
        else
        {
            System.err.println("Not managed :" + req.method());
            ctx.write(rep).addListener(ChannelFutureListener.CLOSE);
        }
    }


    private void sendAnswer(ChannelHandlerContext ctx, DefaultHttpRequest req, FullHttpResponse rep)
    {
        final String cseq = req.headers().get(RtspHeaderNames.CSEQ);
        if (cseq != null)
        {
            rep.headers().add(RtspHeaderNames.CSEQ, cseq);
        }
        final String session = req.headers().get(RtspHeaderNames.SESSION);
        if (session != null)
        {
            rep.headers().add(RtspHeaderNames.SESSION, session);
        }
        if (!HttpHeaders.isKeepAlive(req)) {
            ctx.write(rep).addListener(ChannelFutureListener.CLOSE);
        } else {
            rep.headers().set(RtspHeaderNames.CONNECTION, RtspHeaderValues.KEEP_ALIVE);
            ctx.write(rep);
        }
    }

}

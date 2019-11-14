package com.hserver.core.server.handlers;

import com.hserver.core.server.stat.IpData;
import com.hserver.core.server.stat.RequestData;
import com.hserver.core.server.stat.StatisticsController;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;


import java.util.Deque;
import java.util.Map;


import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONTENT_LENGTH;

/**
 * this class handles the "status" request and builds a response to client with certain statistic
 * information
 * Created by Bess on 27.09.14.
 */
public class StatusHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private StringBuilder responseContent = new StringBuilder();
    StatisticsController controller = new StatisticsController();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        if (req.uri().equals("/status") || req.uri().equals("/status/")) {
            String url = req.uri();
            controller.IncreaseCount();
            controller.addToIpMap(ctx);
            responseContent.setLength(0);
            AppendTable();
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                    Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8)));
            response.headers().set(CONTENT_TYPE, "text/html");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            controller.addToConnectionDeque(ctx, url);
        } else {
            ctx.fireChannelRead(req);
        }
    }

    void AppendTable() {
        responseContent.append("<html>\n" +
                "<head>\n" +
                "<title></title>\n" +
                "<body>\n" +

                "<h3> Total requests number:   " + controller.getCount() + "</h3>" +
                "<h3> Number of the unique requests:   " + controller.getUniqueIpCount() + "</h3>" +
                "<h3> Request Counter for each IP</h3>" +
                "<p><table border=\"2\">\n" +
                "<tr>\n" +
                "<th> IP</th>\n" +
                "<th> Request quantity</th>\n" +
                "<th> Time of last request</th> \n" +
                "</tr>\n");
        Map<String, IpData> src_ip = controller.getIpMap();
        for (String key : src_ip.keySet()) {
            responseContent.append("<tr>\n" +
                    "<td>" + key + "</td>\n" +
                    "<td>" + src_ip.get(key).getCount() + "</td>\n" +
                    "<td>" + src_ip.get(key).getTime() + "</td>\n" +
                    "</tr>\n");
        }
        responseContent.append("</table>  " + "</p>\n");
        responseContent.append("<h3>Number of redirection by Url</h3>" +
                "<table border=\"1\">\n" +
                "<tr>\n" +
                "<th> URL</th>\n" +
                "<th> Redirection quantity</th>\n" +
                "</tr>\n");
        Map<String, Integer> urlMap = controller.getUrlMap();
        for (String key : urlMap.keySet()) {
            responseContent.append("<tr>\n" +
                    "<td>" + key + "</td>\n" +
                    "<td>" + urlMap.get(key) + "</td>\n" +
                    "</tr>\n");
        }
        responseContent.append("</table>  " + "</p>\n");
//        responseContent.append("<h3> Number of opened connections:   " + HelloHandler.getConnectionsCount() + "</h3>");
        responseContent.append("<h3>Log of the last 16 processed connections</h3>" +
                "<table border=\"2\">\n" +
                "<tr>\n" +
                "<th>src_ip</th>\n" +
                "<th>URI</th>\n" +
                "<th>timestamp</th>\n" +
                "<th>sent_bytes</th>\n" +
                "<th>received_bytes</th>\n" +
                "<th>speed (bytes/sec)</th>\n" +
                "</tr>\n");
        Deque<RequestData> last16Connections = StatisticsController.getLogRequestQue();
        for (RequestData d : last16Connections) {
            responseContent.append("<tr>\n" +
                    "<td>" + d.getIp() + "</td>\n" +
                    "<td>" + d.getUrl() + "</td>\n" +
                    "<td>" + d.getTime() + "</td>\n" +
                    "<td>" + d.getSentBytes() + "</td>\n" +
                    "<td>" + d.getReceivedBytes() + "</td>\n" +
                    "<td>" + d.getSpeed() + "</td>\n" +
                    "</tr>\n");
        }
        responseContent.append(
                "</table>  " + "</p>\n" + "</body>\n" + "</html>");
    }

}

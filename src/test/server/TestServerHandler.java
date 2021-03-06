package test.server;

import Protocol.ProtocolBytes;
import ProtocolDispatcher.HandlePlayerMsg;
import com.google.protobuf.BytesValue;
import com.sun.corba.se.impl.encoding.CodeSetConversion;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class TestServerHandler extends ChannelInboundHandlerAdapter {

    // 用于登录的ChannelGroup
    public static ChannelGroup loginChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private HandlePlayerMsg handlePlayerMsg = new HandlePlayerMsg();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        Channel channel = ctx.channel();

        channels.add(channel);
        int count = 0;
        for(Channel ch : channels) {
            count += 1;
        }

        System.out.println("一个连接被加入了！ 目前共有:"+count+"个连接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf result = (ByteBuf) msg;

//        int i = result.readInt();
//
//        byte[] strBytes = new byte[i];
//        result.readBytes(strBytes);
//        System.out.println(new String(strBytes));
//
//
//        float a = result.readFloat();
        ProtocolBytes protocolBytes = ProtocolBytes.Decode(result.array(),0,result.array().length);
        System.out.println("channels.size():"+channels.size());
//        channels.writeAndFlush(protocolBytes.Encde());
        HandleMsg(ctx,protocolBytes);
    }

    private void HandleMsg(ChannelHandlerContext ctx, ProtocolBytes protocolBytes){
        String name = protocolBytes.GetName();
        System.out.println("收到协议，协议名为："+name);
        try {
            // 消息分发至对应的处理方法
            HandlePlayerMsg.class.getMethod("Msg"+name,ChannelGroup.class,Channel.class,ProtocolBytes.class)
                    .invoke(handlePlayerMsg,channels,ctx.channel(),protocolBytes);
        }catch (Exception e){e.printStackTrace();}

    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        Channel incommint = ctx.channel();
        System.out.println("[SERVER] - "+incommint.remoteAddress()+"离开\n");
    }
}

package person.zd.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

public class ServerHandler  extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	
			//do something msg
			String request = (String)msg;  //因为在 Server中设置了“sc.pipeline().addLast(new StringDecoder())”
			System.out.println("Server: " + request);
			String response = "我是"+request+"的响应数据$_";
			//只要调用write进行写入的时候，netty就自动帮我们释放了消息
																		   //注意：传输时必须使用ByteBuf进行传输
			ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes())); //write只是将数据放到buffer缓存里面，flush的时候才写到客户端
			//可以监听什么时候写完
			//.addListener(ChannelFutureListener.CLOSE);  //一次完整的连接会话完成后了自动断开连接（短连接）
														//不加这句话，保持一直连接，可以进行多次会话（长连接）

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}

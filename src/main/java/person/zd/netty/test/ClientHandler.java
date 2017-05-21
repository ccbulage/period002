package person.zd.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelHandlerAdapter {

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	
		System.out.println("client channel active ... "); //在通道刚刚启动的时候会调用这个方法
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		try {
			/*
			 在Client中 ，以ByteBuffer 接受
			
			ByteBuf buf = (ByteBuf)msg;
			byte[] data = new byte[buf.readableBytes()];
			buf.readBytes(data);
			String request = new String(data, "utf-8");
			System.out.println("Client: " + request);
			 */
			
			/*
			  在Client中 ，以String 接收
			 具体做法：在Client中添加   sc.pipeline().addLast(new StringDecoder())
			 */
			String request = (String)msg;
			System.out.println("Client: " + request);
			
			//循环
			//写给服务端（如果加入下面的，就不需要手动进行释放了	，write已帮我们释放了）
//			String response = "我是client反馈得信息";
//			ctx.writeAndFlush(Unpooled.copiedBuffer("333".getBytes()));
			
		} finally {
			//这里客户端只是进行了读数据，所以需要手动进行释放
			ReferenceCountUtil.release(msg); //Netty编程中特殊之处：缓冲区要进行释放，否则会出现指针问题
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}

package person.zd.netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @ClassName: Client
 * @Description: netty客户端，实际编码只需要关注业务（ClientHandler）即可！
 * @author Gene
 * @date 2017年5月13日 上午10:28:39
 */
public class Client {

	public static void main(String[] args) throws Exception {
		//1建立管道连接
		//不用等待别人连接，所以只有一个线程组
		EventLoopGroup workgroup = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(workgroup)
		.channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				//【拆包粘包问题】解决方式一：分隔符法
//				ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
//				sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf));
				
				//【拆包粘包问题】解决方式二：定长法
				sc.pipeline().addLast(new FixedLengthFrameDecoder(5)); //设置定长字符串接收
				
				sc.pipeline().addLast(new StringDecoder());//将ByteBuf类型编码成String
				sc.pipeline().addLast(new ClientHandler());
			}
		});
		
		ChannelFuture cf1 = b.connect("127.0.0.1", 8765).sync();
//		ChannelFuture cf2 = b.connect("127.0.0.1", 8764).sync(); //端口2
		
		//2分隔符法：发送数据
//		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("777$_".getBytes()));
//		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("7778$_".getBytes()));
//		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("77779$_".getBytes()));
		//2定长法：发送数据
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("5555566666".getBytes()));
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("77777".getBytes()));
		
//		cf2.channel().writeAndFlush(Unpooled.copiedBuffer("778".getBytes()));

		
		//下面的结果是555-556-557一次性被发送到服务器端，因此每次write只是写下了缓存里面，flush后才被发送到通道的另一端
//		cf1.channel().write(Unpooled.copiedBuffer("555-".getBytes()));
//		cf1.channel().write(Unpooled.copiedBuffer("556-".getBytes()));
//		cf1.channel().write(Unpooled.copiedBuffer("557".getBytes()));
//		cf1.channel().flush();
		
		
		cf1.channel().closeFuture().sync();
//		cf2.channel().closeFuture().sync();
		workgroup.shutdownGracefully();
		
	}
}

package person.zd.netty.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @ClassName: Server
 * @Description: netty服务端
 * 				其中Line 38之前都是模板代码，只需要添加不同的处理器即可（可以多个），实际编码只需要关注业务（ServerHandler）即可！
 * @author Gene
 * @date 2017年5月13日 上午10:10:44
 */
public class Server {

	public static void main(String[] args) throws Exception {
		//1 第一个线程组 是用于接收Client端连接的
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		//2 第二个线程组 是用于实际的业务处理操作的
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		//3 创建一个辅助类Bootstrap，就是对我们的Server进行一系列的配置
		ServerBootstrap b = new ServerBootstrap(); 
		//把俩个工作线程组加入进来
		b.group(bossGroup, workerGroup)
		//我要指定使用NioServerSocketChannel这种类型的通道,即指定NIO的模式
		.channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG, 1024) //设置tcp缓冲区
		.option(ChannelOption.SO_SNDBUF, 32*1024) //设置发送缓冲区大小
		.option(ChannelOption.SO_RCVBUF, 32*1024) //设置接收缓冲区大小
		.option(ChannelOption.SO_KEEPALIVE, true) //保持连接，默认为true
		//一定要使用 childHandler 去绑定具体的 事件处理器
		.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				
				//【拆包粘包问题】解决方式一：分隔符法   （运用最多）
//				ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());//设置特殊分隔符
//				sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf)); //长度大小与分隔符有关
				
				//【拆包粘包问题】解决方式二：定长法
				sc.pipeline().addLast(new FixedLengthFrameDecoder(5)); //设置定长字符串接收
				
				//【拆包粘包问题】解决方式三：自定义协议（类似TCP传包，限制比较多）
				//...
				
				sc.pipeline().addLast(new StringDecoder());//将ByteBuffer转为String，便于在handler中处理
				sc.pipeline().addLast(new ServerHandler());  //3在这里配置具体接收数据方法的处理
			}
		});

		//绑定指定的端口 进行监听，也可以同时开通其他端口进行接受数据（接受数据的能力变强了，但是处理能力未必变强）
		ChannelFuture f1 = b.bind(8765).sync();  //4 进行绑定只要有Future就说明是一个异步的绑定
//		ChannelFuture f2 = b.bind(8764).sync();
		
		//Thread.sleep(1000000);
		f1.channel().closeFuture().sync();  //5等待关闭（程序阻塞，并且当管道关闭后会异步通知 server）
//		f2.channel().closeFuture().sync();
		
		bossGroup.shutdownGracefully();  //释放资源
		workerGroup.shutdownGracefully();
		 
		
		
	}
	
}

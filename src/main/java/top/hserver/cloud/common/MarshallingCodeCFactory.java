package top.hserver.cloud.common;


import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

public final class MarshallingCodeCFactory {
 
	/*
	 * 创建JBoss Marshalling解码器MarshallingDecoder
	 * @return MarshallingDecoder
	 * */
	
	public static MarshallingDecoder buildMarshallingDecoder() {
		
		final MarshallerFactory marshallingFactory = Marshalling.getProvidedMarshallerFactory("serial");
		
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallingFactory, configuration);
		//构件Netty的marshallingDecoder对象，两个参数分别为Provider和单个消息序列化后的最大长度
		MarshallingDecoder decoder = new MarshallingDecoder(provider,1024*1024*1);
		
		return decoder;
	}
	/*
	 * 创建Marshalling解码器 MarshallingEncoder
	 * return MarshallingEncoder
	 * */
	public static MarshallingEncoder buildMarshallingEncoder() {
		
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		
		MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
		
		MarshallingEncoder encoder = new MarshallingEncoder(provider);
		return encoder;
		
	}
	
}
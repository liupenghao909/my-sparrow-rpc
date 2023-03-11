package com.sparrow.rpc.core.client.proxy;

import com.sparrow.rpc.api.ServiceMetaInfo;
import com.sparrow.rpc.core.RpcTransport;
import com.sparrow.rpc.core.netty.dto.RpcCommand;
import com.sparrow.rpc.core.netty.dto.RpcHeader;
import com.sparrow.rpc.core.netty.dto.RpcRequest;
import com.sparrow.rpc.core.netty.dto.RpcResponse;
import com.sparrow.rpc.core.netty.enums.CommandTypes;
import com.sparrow.rpc.core.netty.enums.RspCode;
import com.sparrow.rpc.core.serialize.SerializeSupport;
import com.sparrow.rpc.core.serialize.SerializerType;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Rpc代理类
 * 是我们创建代理、发送Rpc请求的重要实现类
 * Cglib代理需要实现MethodInterceptor，这样每次调用这个代理类中的方法就会被拦截到MethodInterceptor的intercept方法
 * 重点也就是实现intercept方法
 * @author chengweishen
 * @date 2022/7/17 11:13
 */
public class CglibRpcProxy implements MethodInterceptor {
    private Class clazz;
    private ServiceMetaInfo metaInfo;

    private RpcTransport rpcTransport;

    /**
     * 构造函数
     * @param clz 代理目标的Class
     * @param metaInfo 远程服务元信息（告诉服务端我调用的是哪个服务）
     * @param rpcTransport rpc传输类，主要负责发送请求
     */
    public CglibRpcProxy(Class clz, ServiceMetaInfo metaInfo, RpcTransport rpcTransport) {
        this.clazz = clz;
        this.metaInfo = metaInfo;
        this.rpcTransport = rpcTransport;
    }

    public CglibRpcProxy() {
    }

    private Enhancer enhancer = new Enhancer();

    public Object getProxy() {
        //设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
        RpcRequest rpcRequest = new RpcRequest();
        // 放入调用服务信息
        rpcRequest.setNameSpace(metaInfo.getNameSpace());
        rpcRequest.setServiceName(metaInfo.getServiceName());
        rpcRequest.setMethodName(method.getName());
        //指定用jdk序列化方式
        rpcRequest.setParameters(SerializeSupport.serialize(objects, SerializerType.TYPE_OBJECT_ARRAY.getType()));
        return callRemoteService(rpcRequest);
    }

    private Object callRemoteService(RpcRequest request) {
        RpcCommand rpcCommand = new RpcCommand();
        RpcHeader header = new RpcHeader();
        header.setType(CommandTypes.RPC_REQUEST.getType());
        header.setVersion("v1.0");
        header.setTraceId(UUID.randomUUID().toString());
        rpcCommand.setHeader(header);
        rpcCommand.setData(SerializeSupport.serialize(request));
        try {
            //todo 这个get与RpcResponseHandler的complete可讲
            RpcResponse rpcResponse = rpcTransport.send(rpcCommand).get();
            if (RspCode.SUCCESS.getCode() != rpcResponse.getCode()) {
                throw new RuntimeException(rpcResponse.getErrorMsg());
            }
            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

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
 * @author chengweishen
 * @date 2022/7/17 11:13
 */
public class CglibRpcProxy implements MethodInterceptor {
    private Class clazz;
    private ServiceMetaInfo metaInfo;

    private RpcTransport rpcTransport;

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

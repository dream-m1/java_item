package chat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.commons.io.IOUtils;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer{

    private ThreadLocal<Kryo> kryoTL = ThreadLocal.withInitial(
            (() -> {
                Kryo kryo = new Kryo();
                //设置实例化的方式，先使用无参构造器，失败再使用JVM，
                kryo.setInstantiatorStrategy(
                        new Kryo.DefaultInstantiatorStrategy(
                                new StdInstantiatorStrategy()));
                //注册类，提高效率
                return kryo;
            })
    );



    public byte[] serialize(Object obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        try {
            Kryo kryo = kryoTL.get();
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(baos);
        }

    }

    public  Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Input input = new Input(bais);
        try {
            Kryo kryo = kryoTL.get();
            //必须和kryo.writeObjectOrNull()成对使用
            return kryo.readClassAndObject(input);
        } finally {
            IOUtils.closeQuietly(bais);
            IOUtils.closeQuietly(input);
        }
    }
}

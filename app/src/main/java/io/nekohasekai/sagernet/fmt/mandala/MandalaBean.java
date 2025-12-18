package io.nekohasekai.sagernet.fmt.mandala;

import androidx.annotation.NonNull;

import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;

import org.jetbrains.annotations.NotNull;

import io.nekohasekai.sagernet.fmt.AbstractBean;
import io.nekohasekai.sagernet.fmt.KryoConverters;

public class MandalaBean extends AbstractBean {

    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_TLS = 1;

    public static final int TRANSPORT_TCP = 0;
    public static final int TRANSPORT_WS = 1;

    public String password;

    // TLS Settings
    public int security = SECURITY_NONE;
    public String sni;
    public boolean allowInsecure;

    // Transport Settings
    public int transport = TRANSPORT_TCP;
    public String path; // WebSocket path
    public String host; // WebSocket host

    @Override
    public void initializeDefaultValues() {
        super.initializeDefaultValues();
        if (password == null) password = "";
        if (sni == null) sni = "";
        if (path == null) path = "/";
        if (host == null) host = "";
    }

    @Override
    public void serializeToBuffer(@NonNull ByteBufferOutput output) {
        super.serializeToBuffer(output);
        output.writeString(password);
        output.writeInt(security);
        output.writeString(sni);
        output.writeBoolean(allowInsecure);
        output.writeInt(transport);
        output.writeString(path);
        output.writeString(host);
    }

    @Override
    public void deserializeFromBuffer(@NonNull ByteBufferInput input) {
        super.deserializeFromBuffer(input);
        password = input.readString();
        security = input.readInt();
        sni = input.readString();
        allowInsecure = input.readBoolean();
        transport = input.readInt();
        path = input.readString();
        host = input.readString();
    }

    @NotNull
    @Override
    public AbstractBean clone() {
        return KryoConverters.deserialize(new MandalaBean(), KryoConverters.serialize(this));
    }
}

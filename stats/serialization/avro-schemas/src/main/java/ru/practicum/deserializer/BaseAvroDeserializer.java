package ru.practicum.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.PrincipalDeserializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.practicum.exception.DeserializeException;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final DecoderFactory decoderFactory;
    private final DatumReader<T> reader;

    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        reader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        // Код десериализации двоичных данных
        try {
            if (data != null) {
                BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);
                return this.reader.read(null, decoder);
            } else
                throw new DeserializeException("Ошибка десериализации данных из топика [" + topic + "]: нет данных.");
        } catch (Exception e) {
            throw new PrincipalDeserializationException("Ошибка десериализации данных из топика [" + topic + "]", e);
        }
    }
}

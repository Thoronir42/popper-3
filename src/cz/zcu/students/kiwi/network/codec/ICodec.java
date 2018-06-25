package cz.zcu.students.kiwi.network.codec;

public interface ICodec {

	String decode(String cipher);

	String encode(String plainText);
}

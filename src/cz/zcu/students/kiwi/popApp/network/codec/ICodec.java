package cz.zcu.students.kiwi.popApp.network.codec;

public interface ICodec {

	String decode(String cipher);

	String encode(String plainText);
}

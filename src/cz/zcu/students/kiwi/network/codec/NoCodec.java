package cz.zcu.students.kiwi.network.codec;

public class NoCodec implements ICodec {

	@Override
	public String decode(String cipher) {
		return cipher;
	}

	@Override
	public String encode(String plainText) {
		return plainText;
	}
}

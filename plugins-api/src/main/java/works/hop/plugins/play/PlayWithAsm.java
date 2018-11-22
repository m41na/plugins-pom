package works.hop.plugins.play;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class PlayWithAsm {

	public static void main(String[] args) throws IOException, ClassNotFoundException, ReflectiveOperationException {
		printPlayStuff();
		playStuffBytes();
		playStuffProxy();
	}

	public static void printPlayStuff() throws IOException {
		PrintVisitor cp = new PrintVisitor();
		ClassReader cr = new ClassReader("works.hop.plugins.play.PlayStuff");
		cr.accept(cp, 0);
	}
	
	public static void playStuffBytes() throws IOException {
		ClassReader cr = new ClassReader("works.hop.plugins.play.PlayStuff");
		ClassWriter cw = new ClassWriter(cr, 0);
		//cv forwarfs a;; events to c
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM6, cw) {
		};
		cr.accept(cv, 0);
		byte[] bytes = cw.toByteArray();
		System.out.println("length of bytes reads is " + bytes.length);
	}
	
	public static void playStuffProxy() throws ClassNotFoundException, ReflectiveOperationException{
		ClassLoader cl = new FindAClass(PlayWithAsm.class.getClassLoader());
		Class<?> playClazz = Class.forName("works.hop.plugins.play.PlayStuff", true, cl);
		Object play = PlayProxy.instance(playClazz);
		playClazz.getMethod("setName", String.class).invoke(play, "James");
		System.out.println(playClazz.getMethod("getName").invoke(play));
	}
}

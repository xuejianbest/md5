package com.lwt.md5;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class MD5 {
	private String content;
	private File file;
	private String md5;
	
	public MD5(String content){
		if(content == null)
			return;
		this.content = content;
	}
	public MD5(File file){
		if(file == null)
			return;
		this.file = file;
	}
	
	public String md5(){
		this.go();
		return md5;
	}
	
	private void go(){
		int[] init = new int[]{0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476};
		
		byte[] bytes = new byte[64];
		byte[] tail = new byte[0];
		int len = 0;
		long size = 0;
		if(file != null){
			BufferedInputStream reader = null;
			try {
				reader = new BufferedInputStream(new FileInputStream(file));
				while((len = reader.read(bytes)) != -1){
					if(len == 64){
						init = md5_2(bytes, init);
						size += 512;
					}else{
						tail = Arrays.copyOfRange(bytes, 0, len);
						size += len * 8;
						break;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(reader != null){
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else {
			byte[] src = content.getBytes();
			int n = src.length / 64;
			for(int i=0; i<n; i++){
				bytes = Arrays.copyOfRange(src, i*64, i*64+64);
				init = md5_2(bytes, init);
			}
			size = src.length * 8;
			tail = Arrays.copyOfRange(src, src.length - src.length % 64, src.length);
		}
		
		
		if(tail.length < 56){
			bytes = Arrays.copyOf(tail, 64);
			bytes[tail.length] = -128;
		}else{
			bytes = Arrays.copyOf(tail, 64);
			bytes[tail.length] = -128;
			init = md5_2(bytes, init);
			bytes = Arrays.copyOf(new byte[]{}, 64);
		}
		for(int i=0; i<8; i++){
			bytes[56+i] = new Long(size >>> i*8).byteValue();
		}
		init = md5_2(bytes, init);
		md5 = int2string(init[0]) + int2string(init[1]) + int2string(init[2]) + int2string(init[3]);
	}

	public int[] md5_2(byte[] bytes, int[] before) {
		int A = before[0], B = before[1], C = before[2], D = before[3];
		int a = A, b = B, c = C, d = D;
		int s[] = { 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
				5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 4, 11,
				16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 6, 10, 15,
				21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21 };
		int[] k = { 0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee, 0xf57c0faf,
				0x4787c62a, 0xa8304613, 0xfd469501, 0x698098d8, 0x8b44f7af,
				0xffff5bb1, 0x895cd7be, 0x6b901122, 0xfd987193, 0xa679438e,
				0x49b40821, 0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
				0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8, 0x21e1cde6,
				0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905, 0xfcefa3f8,
				0x676f02d9, 0x8d2a4c8a, 0xfffa3942, 0x8771f681, 0x6d9d6122,
				0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
				0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05, 0xd9d4d039,
				0xe6db99e5, 0x1fa27cf8, 0xc4ac5665, 0xf4292244, 0x432aff97,
				0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92, 0xffeff47d,
				0x85845dd1, 0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
				0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391 };

		for (int i = 0; i < 64; i++) {
			int f, g;
			if (i < 16) {
				f = (b & c) | (~b & d);
				g = i;
			} else if (i < 32) {
				f = (b & d) | (~d & c);
				g = (5 * i + 1) % 16;
			} else if (i < 48) {
				f = b ^ c ^ d;
				g = (3 * i + 5) % 16;
			} else {
				f = c ^ (~d | b);
				g = 7 * i % 16;
			}

			int m = byteArr2Int(Arrays.copyOfRange(bytes, 4*g, 4*g + 4));
			int b_temp = b;
			b = b + Integer.rotateLeft(a + f + m + k[i], s[i]);
			a = d;
			d = c;
			c = b_temp;
		}
		A += a;
		B += b;
		C += c;
		D += d;

		int[] res = new int[4];
		res[0] = A;
		res[1] = B;
		res[2] = C;
		res[3] = D;
		
		return res;
	}

	public String int2string(int n){
		String res = "";
		for(int i=0; i<4; i++){
			String s = (Integer.toHexString((n >>> (8 * i)) & 0xff)); 
			if(s.length()<2){
				s = "0" + s;
			}
			res += s;
		}
		return res;
	}

	private int byteArr2Int(byte[] bytes){
		int res = 0;
		for(int i=3; i>=0; i--){
			res = (res << 8);
			res += (int)bytes[i] & 0xff;
		}
		return res;
	}
	
	
	@Override
	public String toString() {
		return "MD5 [content=" + content + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MD5 other = (MD5) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		return true;
	}

}

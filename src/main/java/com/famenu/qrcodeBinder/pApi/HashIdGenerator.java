package com.famenu.qrcodeBinder.pApi;

public class HashIdGenerator {
	final static int HASH_LENGTH=5;

	final static String ALPHABET="nwLtlUzjXFmIxh9gJsdyMGOD0rv264ZcK7bYCE3HRSuBT8fVo1PkipQaNWq5eA";
	final static int[] SHUFFLE_SEQ_A=new int[]{18,21,3,8,13,14,9,0,6,28,11,2,5,27,23,4,22,12,10,20,15,26,7,25,19,24,1,17,16};
	final static int[] SHUFFLE_SEQ_B=new int[]{5,6,25,19,0,16,18,10,13,22,26,9,7,15,28,14,1,12,17,11,23,27,3,2,24,4,21,8,20};
	final static int[] SHUFFLE_SEQ_HASH=new int[]{2,4,1,3,0};

	//0b00010000001011010101011101101
	final static long XOR_29_A=0x000000000205AAED;
	//0b01011101101000010011001100000
	final static long XOR_29_B=0x000000000BB42660;
	
	private StringBuilder strbuff=new StringBuilder();

	/**
	 * shuffle first 29 bits of a number using the static swapping sequence
	 */
	private long shuffle(long number, int[] swap_seq){
		byte [] orig_num=new byte[swap_seq.length];
		byte [] shuffled_num=new byte[swap_seq.length];
		
		//unpack number into array. i am interested only in rightmost bit for every array item, so i don't care for signed shift
		for(int i=0; i<orig_num.length; i++){
			orig_num[i]= (byte) (number >>> i );
		}
		
		//move elements into new shuffled array
		for(int i=0; i<shuffled_num.length; i++){
			shuffled_num[i]=orig_num[swap_seq[i]];
		}
		
		//repack into a long
		number=0;
		for(int i=0; i<shuffled_num.length; i++){
			number+= (shuffled_num[i] & 1) << i;
		}
		
		return number;
	}
	
	private String shuffle(String seq, int[] swap_seq){
		strbuff.setLength(0);
		//move elements into new shuffled array
		for(int i=0; i<swap_seq.length; i++){
			strbuff.append(seq.charAt(swap_seq[i]));
		}
		
		return strbuff.toString();
	}
	
	
	/**
	 * Algoritmo per la conversione di base. la base e` decisa dalla lunghezza dell'alfabeto
	 */
	private String translate(long number){
		strbuff.setLength(0);

		//bit shuffling to augment chaos behavior
		number=shuffle(number, SHUFFLE_SEQ_A);
		number=number^XOR_29_A;
		number=shuffle(number, SHUFFLE_SEQ_B);
		number=number^XOR_29_B;
		number=shuffle(number, SHUFFLE_SEQ_A);
/*		number=number^XOR_29_A;
		number=fixedShuffle29(number, SHUFFLE_SEQ_B);
		number=number^XOR_29_B;
*/		
		//conversion to base 62
		int mod = 0;
		long base = ALPHABET.length();
		while( number != 0 ) {
			mod = (int) (number % base);
			strbuff.append(ALPHABET.charAt(mod));
			number = number / base;
		}
		
		//padding with zero
		char zero=ALPHABET.charAt(0);
		for(int i=strbuff.length(); i<HASH_LENGTH; i++){
			strbuff.append(zero);
		}
	
		//hide less chaotic characters
		return shuffle(strbuff.toString(), SHUFFLE_SEQ_HASH);
	}

	public static void main(String[] args){
		HashIdGenerator nn=new HashIdGenerator();
		for(int i=0; i<8000; i++){
			System.out.println(nn.translate(i));
		}
	}
}

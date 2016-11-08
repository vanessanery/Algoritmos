package br.uniceub.hygor;

public class No {

	private final int ASCII = 256;

	private char ch;
	private int frequencia;
	private No esquerdo, direito;

	public No(char ch, int frequencia, No esquerdo, No direito){
		this.ch = ch;
		this.frequencia = frequencia;
		this.esquerdo = esquerdo;
		this.direito = direito;
	}

	public boolean checaNo(){
		assert ((this.esquerdo == null) && (this.direito == null)) || ((this.esquerdo != null) && (this.direito != null));
		return (this.esquerdo == null) && (this.direito == null);
	}

	public int comparar(No outro){
		return this.frequencia - outro.getFrequencia();
	}

	public void compress() {
		// read the input
		String s = BinaryStdIn.readString();
		char[] input = s.toCharArray();

		// tabulate frequency counts
		int[] freq = new int[ASCII];
		for (int i = 0; i < input.length; i++)
			freq[input[i]]++;

		// build Huffman trie
		No raiz = montarArvore(freq);

		// build code table
		String[] st = new String[ASCII];
		montarCodigo(st, raiz, "");

		// print trie for decoder
		escreverArvore(raiz);

		// print number of bytes in original uncompressed message
		BinaryStdOut.write(input.length);

		// use Huffman code to encode input
		for (int i = 0; i < input.length; i++) {
			String code = st[input[i]];
			for (int j = 0; j < code.length(); j++) {
				if (code.charAt(j) == '0') {
					BinaryStdOut.write(false);
				}
				else if (code.charAt(j) == '1') {
					BinaryStdOut.write(true);
				}
				else throw new IllegalStateException("Houve um erro");
			}
		}

		// close output stream
		BinaryStdOut.close();
	}

	public No montarArvore(int[] freq) {

		// initialze priority queue with singleton trees
		MinPQ<No> pq = new MinPQ<No>();
		for (char i = 0; i < ASCII; i++)
			if (freq[i] > 0)
				pq.insert(new No(i, freq[i], null, null));

		// special case in case there is only one character with a nonzero frequency
		if (pq.size() == 1) {
			if (freq['\0'] == 0) pq.insert(new No('\0', 0, null, null));
			else                 pq.insert(new No('\1', 0, null, null));
		}

		// merge two smallest trees
		while (pq.size() > 1) {
			No left  = pq.delMin();
			No right = pq.delMin();
			No parent = new No('\0', left.frequencia + right.frequencia, left, right);
			pq.insert(parent);
		}
		return pq.delMin();
	}

	public void escreverArvore(No x) {
		if (x.checaNo()) {
			BinaryStdOut.write(true);
			BinaryStdOut.write(x.ch, 8);
			return;
		}
		BinaryStdOut.write(false);
		escreverArvore(x.esquerdo);
		escreverArvore(x.direito);
	}

	public void montarCodigo(String[] st, No x, String s) {
		if (!x.checaNo()) {
			montarCodigo(st, x.esquerdo,  s + '0');
			montarCodigo(st, x.direito, s + '1');
		}
		else {
			st[x.ch] = s;
		}
	}

	/**
	 * Reads a sequence of bits that represents a Huffman-compressed message from
	 * standard input; expands them; and writes the results to standard output.
	 */
	public void expandir() {

		// read in Huffman trie from input stream
		No root = lerRaiz(); 

		// number of bytes to write
		int length = BinaryStdIn.readInt();

		// decode using the Huffman trie
		for (int i = 0; i < length; i++) {
			No x = root;
			while (!x.checaNo()) {
				boolean bit = BinaryStdIn.readBoolean();
				if (bit) x = x.direito;
				else     x = x.esquerdo;
			}
			BinaryStdOut.write(x.ch, 8);
		}
		BinaryStdOut.close();
	}


	private static No lerRaiz() {
		boolean checaNo = BinaryStdIn.readBoolean();
		if (checaNo) {
			return new No(BinaryStdIn.readChar(), -1, null, null);
		}
		else {
			return new No('\0', -1, lerRaiz(), lerRaiz());
		}
	}



	/* GETTERS E SETTERS */

	public int getFrequencia(){
		return this.frequencia;
	}

}

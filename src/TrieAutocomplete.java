import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * General trie/priority queue algorithm for implementing Autocompletor
 * 
 * @author Austin Lu
 *
 */
public class TrieAutocomplete implements Autocompletor {

	/**
	 * Root of entire trie
	 */
	protected Node myRoot;

	/**
	 * Constructor method for TrieAutocomplete. Should initialize the trie
	 * rooted at myRoot, as well as add all nodes necessary to represent the
	 * words in terms.
	 * 
	 * @param terms
	 *            - The words we will autocomplete from
	 * @param weights
	 *            - Their weights, such that terms[i] has weight weights[i].
	 * @throws a
	 *             NullPointerException if either argument is null
	 */
	public TrieAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null)
			throw new NullPointerException("One or more arguments null");
		myRoot = new Node('-', null, 0);

		for (int i = 0; i < terms.length; i++) {
			add(terms[i], weights[i]);
		}
	}

	/**
	 * Add the word with given weight to the trie. If word already exists in the
	 * trie, no new nodes should be created, but the weight of word should be
	 * updated.
	 * 
	 * In adding a word, this method should do the following: Create any
	 * necessary intermediate nodes if they do not exist. Update the
	 * subtreeMaxWeight of all nodes in the path from root to the node
	 * representing word. Set the value of myWord, myWeight, isWord, and
	 * mySubtreeMaxWeight of the node corresponding to the added word to the
	 * correct values
	 * 
	 * @throws a
	 *             NullPointerException if word is null
	 * @throws an
	 *             IllegalArgumentException if weight is negative.
	 * 
	 */
	private void add(String word, double weight) {
		// TODO: Implement add
		if (word == null) {
			throw new NullPointerException();
		}
		if (weight < 0) {
			throw new IllegalArgumentException();
		}

		Node current = myRoot;

		for (int i = 0; i < word.length(); i++) {
			char ch = word.charAt(i);
			if (current.mySubtreeMaxWeight < weight) {
				current.mySubtreeMaxWeight = weight;
			}
			if (!current.children.containsKey(ch)) {
				current.children.put(ch, new Node(ch, current, weight));
			}
			current = current.children.get(ch);
		}

		current.isWord = true;

		double oweight = current.myWeight;

		current.myWeight = weight;

		if (current.mySubtreeMaxWeight < weight) {
			current.mySubtreeMaxWeight = weight;
		}
		if (oweight > weight) {
			double gweight = current.mySubtreeMaxWeight;
			while (current.parent != null) {
				if (current.mySubtreeMaxWeight > gweight) {
					gweight = current.mySubtreeMaxWeight;
				}
				current.mySubtreeMaxWeight = gweight;
				current = current.parent;
			}
		}
		current.myWord = word;
	}

	@Override
	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in the trie with the largest weight which match the given prefix,
	 * in descending weight order. If less than k words exist matching the given
	 * prefix (including if no words exist), then the array instead contains all
	 * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
	 * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
	 * 2) should return {"air"}
	 * 
	 * @param prefix
	 *            - A prefix which all returned words must start with
	 * @param k
	 *            - The (maximum) number of words to be returned
	 * @return An array of the k words with the largest weights among all words
	 *         starting with prefix, in descending weight order. If less than k
	 *         such words exist, return an array containing all those words If
	 *         no such words exist, return an empty array
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public String[] topKMatches(String prefix, int k) {
		// TODO: Implement topKMatches
		if (prefix == null) {
			throw new NullPointerException();
		}
		String[] empty = new String[0];
		ArrayList<String> words = new ArrayList<String>();
		PriorityQueue<Node> Q = new PriorityQueue<Node>(k,
				new Node.ReverseSubtreeMaxWeightComparator());
		Node current = myRoot;
		for (int i = 0; i < prefix.length(); i++) {
			if (current.getChild(prefix.charAt(i)) != null) {
				current = current.getChild(prefix.charAt(i));
			} else {
				return empty;
			}
		}
		Q.add(current);
		while (!Q.isEmpty() && words.size() < k) {
			current = Q.poll();
			if (current.isWord) {
				words.add(current.myWord);
			}
			for (Node chl : current.children.values()) {
				Q.add(chl);
			}
		}
		String[] ans = new String[words.size()];
		ans = words.toArray(ans);
		return ans;
	}

	@Override
	/**
	 * Given a prefix, returns the largest-weight word in the trie starting with
	 * that prefix.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from _terms with the largest weight starting with
	 *         prefix, or an empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 * 
	 */
	public String topMatch(String prefix) {
		// TODO: Implement topMatch
		String empty = "";
		if (prefix == null) {
			throw new NullPointerException();
		}
		Node current = myRoot;
		for (int i = 0; i < prefix.length(); i++) {
			if (current.getChild(prefix.charAt(i)) != null) {
				current = current.getChild(prefix.charAt(i));
			} else {
				return empty;
			}
		}

		while (current.mySubtreeMaxWeight != current.myWeight) {
			for (Node chld : current.children.values()) {
				if (chld.mySubtreeMaxWeight == current.mySubtreeMaxWeight) {
					current = chld;
					break;
				}
			}
		}
		return current.myWord;
	}

}

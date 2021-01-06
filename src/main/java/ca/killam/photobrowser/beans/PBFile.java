package ca.killam.photobrowser.beans;

import lombok.Data;

/**
 * Provides a mechanism for passing a descriptive string and a path in a class.
 * The paths should be encdoded with \'s replaced with three tildas (~'s).
 * 
 * @author arkillam
 */

@Data
public class PBFile {

	/**
	 * Takes a string with ~~~'s instead of \'s and restores it.
	 * 
	 * @param s
	 * @return
	 */
	public static String decode(String s) {
		return s.replaceAll("~~~", "\\\\");
	}

	/**
	 * Takes a string with \'s and "encodes" them into ~~~~'s
	 * 
	 * @param s
	 * @return
	 */
	public static String encode(String s) {
		return s.replaceAll("\\\\", "~~~");
	}

	private String descriptiveName;

	private String encodedPath;

	/** e.g. jpg, png */
	private String filetype;

	/** is it a directory? */
	private boolean dir = false;

	/** Base64-encoded binary content (intended to pass images around) */
	private String content;

	/** Base64-encoded thumbnail image */
	private String thumbnail;

	/**
	 * @param descriptiveName
	 * @param encodedPath     if this contains \'s, they will be replaced with ~~~'s
	 */
	public PBFile(String descriptiveName, String encodedPath) {
		this.descriptiveName = descriptiveName;
		this.encodedPath = encode(encodedPath);
	}

}

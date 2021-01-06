package ca.killam.photobrowser.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.killam.photobrowser.beans.PBFile;
import net.coobird.thumbnailator.Thumbnails;

@Controller
public class PhotoBrowserController {

	/** measured in entries, not megabytes */
	private static final int MAX_CACHE_SIZE = 500;

	/** caches images */
	private Map<String, PBFile> imageCache = new HashMap<>();

	@Value("${andrew.root-dir}")
	private String rootDir = null;

	Logger logger = LoggerFactory.getLogger(PhotoBrowserController.class);

	/**
	 * Creates a PBFile object representing a file. Files are expected to be
	 * directories, JPGs or PNGs. Caching is used.
	 * 
	 * @param f
	 * 
	 * @return a PBFile, or null for unsupported file types
	 * 
	 * @throws Exception
	 * 
	 */
	private PBFile createPBFile(File f) throws Exception {

		if (!imageCache.containsKey(f.getAbsolutePath())) {

			PBFile p = new PBFile(f.getName(), f.getAbsolutePath());

			if (f.isDirectory()) {
				p.setDir(f.isDirectory());
			} else {

				// TODO: cover a broader range of file types
				p.setFiletype("png");
				if (f.getAbsoluteFile().toString().endsWith("jpg")) {
					p.setFiletype("jpeg");
				} else if (f.getAbsoluteFile().toString().endsWith("png")) {
					p.setFiletype("png");
				} else {
					return null;
				}

				// load the image, convert it to Base64, and store it
				logger.debug("starting to encode " + f.getAbsolutePath());
				byte[] fileContent = FileUtils.readFileToByteArray(f);
				String encodedString = Base64.getEncoder().encodeToString(fileContent);
				p.setContent(encodedString);

				// create a 100x100 pixel thumb nail and encode it in Base64
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				Thumbnails.of(f).size(100, 100).outputFormat("JPEG").outputQuality(1).toOutputStream(outputStream);
				byte[] data = outputStream.toByteArray();
				String encodedThumbnail = Base64.getEncoder().encodeToString(data);
				p.setThumbnail(encodedThumbnail);
			}

			logger.debug("created and cached: " + p.getDescriptiveName());
			imageCache.put(f.getAbsolutePath(), p);
		}

		return imageCache.get(f.getAbsolutePath());
	}

	/**
	 * @param model
	 * @param dir   the directory we are looking in
	 * 
	 * @return name of the JSP
	 */
	@GetMapping("/")
	public String home(Model model, @RequestParam(required = false) String dir) throws Exception {

		if (dir == null || dir.trim().length() < 1) {
			dir = rootDir;
		}
		logger.debug("dir: " + dir);

		File directory = new File(PBFile.decode(dir));

		model.addAttribute("dir", directory.getAbsolutePath());

		// get the directory listing, add it to the model
		File files[] = directory.listFiles();

		// package up the listing
		List<PBFile> pbfiles = new ArrayList<>();
		for (File f : files) {

			PBFile p = createPBFile(f);
			if (p == null) {
				// indicates an unsupported file type, PDFs for example
				continue;
			}

			// we are creating a copy of the image without its full-size content to save
			// memory
			PBFile clone = new PBFile(p.getDescriptiveName(), p.getEncodedPath());
			clone.setContent(null);
			clone.setDir(p.isDir());
			clone.setFiletype(p.getFiletype());
			clone.setThumbnail(p.getThumbnail());

			pbfiles.add(clone);
		}

		model.addAttribute("files", pbfiles);

		// if cache gets too big, clear it
		if (imageCache.size() > MAX_CACHE_SIZE) {
			logger.warn("clearing cache");
			imageCache.clear();
		}

		return "Home";
	}

	/**
	 * @param model
	 * @param path  the image we are displaying
	 * 
	 * @return name of the JSP
	 */
	@GetMapping("/single")
	public String single(Model model, @RequestParam(required = true) String path) throws Exception {

		File f = new File(PBFile.decode(path));

		// TODO: handle null return type
		PBFile pbfile = createPBFile(f);
		model.addAttribute("file", pbfile);

		model.addAttribute("path", f.getAbsolutePath());

		// if cache gets too big, clear it
		if (imageCache.size() > MAX_CACHE_SIZE) {
			logger.warn("clearing cache");
			imageCache.clear();
		}

		return "Single";
	}

}

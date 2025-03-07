package com.example.zexus_music_streaming.service;

import com.example.zexus_music_streaming.model.DownloadCount;
import com.example.zexus_music_streaming.model.MusicFile;
import com.example.zexus_music_streaming.model.SponsoredPlaylist;
import com.example.zexus_music_streaming.repository.DownloadCountRepository;
import com.example.zexus_music_streaming.repository.MusicFileRepository;
import com.example.zexus_music_streaming.repository.SponsoredPlaylistRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MusicService {

    private final MusicFileRepository musicFileRepository;
    private final DownloadCountRepository downloadCountRepository;
    private final SponsoredPlaylistRepository sponsoredPlaylistRepository;

    // Single constructor for dependency injection
    public MusicService(MusicFileRepository musicFileRepository,
                        DownloadCountRepository downloadCountRepository,
                        SponsoredPlaylistRepository sponsoredPlaylistRepository) {
        this.musicFileRepository = musicFileRepository;
        this.downloadCountRepository = downloadCountRepository;
        this.sponsoredPlaylistRepository = sponsoredPlaylistRepository;
    }

    // Admin adds a song to the sponsored playlist
    public void addToSponsoredPlaylist(String songTitle, String artistName) {
        SponsoredPlaylist sponsoredPlaylist = new SponsoredPlaylist();
        sponsoredPlaylist.setSongTitle(songTitle);
        sponsoredPlaylist.setArtistName(artistName);
        sponsoredPlaylistRepository.save(sponsoredPlaylist);
    }

    private static final String SONG_UPLOAD_DIR = "D:/Backend/uploads/songs/";
    private static final String ARTWORK_UPLOAD_DIR = "D:/Backend/uploads/artworks/";

    // Get Top 10 songs by download count
    public List<MusicFile> getTop10SongsByDownloadCount() {
        return musicFileRepository.findTop10SongsByDownloadCount();
    }

    // Get Top 100 songs by download count
    public List<MusicFile> getTop100SongsByDownloadCount() {
        return musicFileRepository.findTop100SongsByDownloadCount();
    }

    // Get most recent uploaded songs
    public List<MusicFile> getRecentUploadedSongs() {
        return musicFileRepository.findTop10ByOrderByYearDesc();
    }

    // Get playlist by genre (Top songs within a genre)
    public List<MusicFile> getPlaylistByGenre(String genre) {
        return musicFileRepository.findTopSongsByGenre(genre);
    }

    // Get songs by genre
    public List<MusicFile> getFilesByGenre(String genre) {
        return musicFileRepository.findByGenre(genre);
    }

    // Save method for music file with genre
    public void save(MultipartFile file, MultipartFile artwork, String artistName, String songTitle, String year, String producer, String featuring, String album, String genre) throws IOException {
        // Ensure directories exist
        Files.createDirectories(Paths.get(SONG_UPLOAD_DIR));
        Files.createDirectories(Paths.get(ARTWORK_UPLOAD_DIR));

        // Sanitize file names
        String uniqueFileName = generateUniqueFilename(file);

        // Save the song file
        Path filePath = Paths.get(SONG_UPLOAD_DIR + uniqueFileName);
        file.transferTo(filePath.toFile());

        // Handle artwork upload
        String artworkUrl = handleArtworkUpload(artwork);

        // Save metadata to the database
        MusicFile musicFile = new MusicFile();
        musicFile.setFileName(uniqueFileName);
        musicFile.setFileType(file.getContentType());
        musicFile.setSize(file.getSize());
        musicFile.setUrl("/uploads/songs/" + uniqueFileName);
        musicFile.setArtistName(artistName);
        musicFile.setSongTitle(songTitle);
        musicFile.setYear(year);
        musicFile.setProducer(producer);
        musicFile.setFeaturing(featuring);
        musicFile.setAlbum(album);
        musicFile.setArtworkUrl(artworkUrl);
        musicFile.setGenre(genre); // Set genre

        musicFileRepository.save(musicFile);

        // Initialize Download Count
        DownloadCount downloadCount = new DownloadCount();
        downloadCount.setMusicFile(musicFile);
        downloadCount.setCount(0);
        downloadCountRepository.save(downloadCount);
    }

    // Method to handle artwork upload and return URL
    private String handleArtworkUpload(MultipartFile artwork) throws IOException {
        if (artwork != null && !artwork.isEmpty()) {
            String artworkFilename = generateUniqueFilename(artwork);
            Path artworkPath = Paths.get(ARTWORK_UPLOAD_DIR + artworkFilename);
            artwork.transferTo(artworkPath.toFile());
            return "/uploads/artworks/" + artworkFilename;
        }
        return null;
    }

    // Generate a unique filename for uploaded files
    private String generateUniqueFilename(MultipartFile file) {
        String sanitizedFileName = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("[^a-zA-Z0-9.\\-_]", "_");
        return System.currentTimeMillis() + "_" + sanitizedFileName;
    }

    // Fetch all files (can be used in controller to list all songs)
    public List<MusicFile> getAllFiles() {
        return musicFileRepository.findAll();
    }

    // Fetch a specific file by ID (can be used in controller for detailed view)
    public Optional<MusicFile> getFileById(Long id) {
        return musicFileRepository.findById(id);
    }

    // Increment download count for a song (can be used to track downloads)
    public void incrementDownloadCount(MusicFile musicFile) {
        DownloadCount downloadCount = downloadCountRepository.findById(musicFile.getId())
                .orElseThrow(() -> new RuntimeException("Download count not found for song ID: " + musicFile.getId()));
        downloadCount.setCount(downloadCount.getCount() + 1);
        downloadCountRepository.save(downloadCount);
    }

    // Fetch songs by title (case-insensitive search)
    public List<MusicFile> getFilesByTitle(String songTitle) {
        return musicFileRepository.findBySongTitleContainingIgnoreCase(songTitle);
    }

    // Fetch songs by artist name (case-insensitive search)
    public List<MusicFile> getFilesByArtist(String artistName) {
        return musicFileRepository.findByArtistNameContainingIgnoreCase(artistName);
    }
}
package com.example.zexus_music_streaming.controller;

import com.example.zexus_music_streaming.model.MusicFile;
import com.example.zexus_music_streaming.service.MusicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/songs")
public class SongUploadController {

    private final MusicService musicService;

    public SongUploadController(MusicService musicService) {
        this.musicService = musicService;
    }

    // Get Top 10 Songs by Downloads
    @GetMapping("/top10")
    public ResponseEntity<List<MusicFile>> getTop10SongsByDownloadCount() {
        List<MusicFile> musicFiles = musicService.getTop10SongsByDownloadCount();
        return ResponseEntity.ok(musicFiles);
    }

    // Get Top 100 Songs by Downloads
    @GetMapping("/top100")
    public ResponseEntity<List<MusicFile>> getTop100SongsByDownloadCount() {
        List<MusicFile> musicFiles = musicService.getTop100SongsByDownloadCount();
        return ResponseEntity.ok(musicFiles);
    }

    // Get Most Recent Uploaded Songs
    @GetMapping("/recent")
    public ResponseEntity<List<MusicFile>> getRecentUploadedSongs() {
        List<MusicFile> musicFiles = musicService.getRecentUploadedSongs();
        return ResponseEntity.ok(musicFiles);
    }

    // Get Playlist by Genre
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MusicFile>> getPlaylistByGenre(@PathVariable String genre) {
        List<MusicFile> musicFiles = musicService.getPlaylistByGenre(genre);
        return ResponseEntity.ok(musicFiles);
    }

    // Get songs by genre
    @GetMapping("/songsByGenre/{genre}")
    public ResponseEntity<List<MusicFile>> getFilesByGenre(@PathVariable String genre) {
        List<MusicFile> musicFiles = musicService.getFilesByGenre(genre);
        return ResponseEntity.ok(musicFiles);
    }

    // Admin adds a song to the sponsored playlist
    @PostMapping("/sponsored")
    public ResponseEntity<String> addToSponsoredPlaylist(@RequestParam("songTitle") String songTitle,
                                                         @RequestParam("artistName") String artistName) {
        musicService.addToSponsoredPlaylist(songTitle, artistName);
        return ResponseEntity.ok("Song added to sponsored playlist.");
    }

    // Handle file upload for songs and metadata (including artwork and genre)
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("artistName") String artistName,
            @RequestParam("songTitle") String songTitle,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "producer", required = false) String producer,
            @RequestParam(value = "featuring", required = false) String featuring,
            @RequestParam(value = "album", required = false) String album,
            @RequestParam("genre") String genre,
            @RequestParam(value = "artwork", required = false) MultipartFile artwork
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty. Please upload a valid song file.");
        }

        // Validate file type (e.g., MP3, WAV)
        String fileType = file.getContentType();
        if (fileType == null || (!fileType.equals("audio/mpeg") && !fileType.equals("audio/wav"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid file type. Only MP3 and WAV are allowed.");
        }

        try {
            // Use MusicService to handle the file and metadata saving, including artwork and genre
            musicService.save(file, artwork, artistName, songTitle, year, producer, featuring, album, genre);

            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    // Endpoint to get all songs
    @GetMapping("/")
    public ResponseEntity<List<MusicFile>> getAllFiles() {
        List<MusicFile> files = musicService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    // Endpoint to get a song by ID
    @GetMapping("/{id}")
    public ResponseEntity<MusicFile> getFileById(@PathVariable Long id) {
        Optional<MusicFile> musicFile = musicService.getFileById(id);
        return musicFile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Endpoint to get songs by title (case-insensitive)
    @GetMapping("/title/{songTitle}")
    public ResponseEntity<List<MusicFile>> getFileByTitle(@PathVariable String songTitle) {
        List<MusicFile> musicFiles = musicService.getFilesByTitle(songTitle);
        if (musicFiles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(musicFiles);
    }

    // Endpoint to increment download count for a song
    @PostMapping("/download/{id}")
    public ResponseEntity<String> incrementDownloadCount(@PathVariable Long id) {
        Optional<MusicFile> musicFile = musicService.getFileById(id);
        if (musicFile.isPresent()) {
            musicService.incrementDownloadCount(musicFile.get());
            return ResponseEntity.ok("Download count incremented for song: " + musicFile.get().getSongTitle());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Song not found.");
    }

    // Endpoint to get songs by artist name (case-insensitive)
    @GetMapping("/artist/{artistName}")
    public ResponseEntity<List<MusicFile>> getFilesByArtist(@PathVariable String artistName) {
        List<MusicFile> musicFiles = musicService.getFilesByArtist(artistName);
        if (musicFiles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(musicFiles);
    }
}
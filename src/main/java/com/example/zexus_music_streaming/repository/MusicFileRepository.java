package com.example.zexus_music_streaming.repository;

import com.example.zexus_music_streaming.model.MusicFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicFileRepository extends JpaRepository<MusicFile, Long> {

    // Get Top 10 songs by download count (simplified)
    @Query("SELECT m FROM MusicFile m JOIN m.downloadCount d ORDER BY d.count DESC")
    List<MusicFile> findTop10SongsByDownloadCount();

    // Get Top 100 songs by download count (simplified)
    @Query("SELECT m FROM MusicFile m JOIN m.downloadCount d ORDER BY d.count DESC")
    List<MusicFile> findTop100SongsByDownloadCount();

    // Get most recent uploaded songs (based on the year)
    List<MusicFile> findTop10ByOrderByYearDesc();

    // Get playlist by genre, sorted by download count
    @Query("SELECT m FROM MusicFile m WHERE m.genre = :genre ORDER BY m.downloadCount.count DESC")
    List<MusicFile> findTopSongsByGenre(String genre);

    // Find songs by genre
    List<MusicFile> findByGenre(String genre);

    // Find all songs sorted by number of downloads
    @Query("SELECT m FROM MusicFile m JOIN m.downloadCount d ORDER BY d.count DESC")
    List<MusicFile> findSongsByDownloadCount();

    // Find songs by release year (you can use the year field as the date)
    List<MusicFile> findByYear(String year);

    // Find a song by its title (case-insensitive search)
    Optional<MusicFile> findBySongTitleIgnoreCase(String songTitle);

    // Find songs by song title (case-insensitive search)
    List<MusicFile> findBySongTitleContainingIgnoreCase(String songTitle);

    // Find songs by artist name (case-insensitive search)
    List<MusicFile> findByArtistNameContainingIgnoreCase(String artistName);

}

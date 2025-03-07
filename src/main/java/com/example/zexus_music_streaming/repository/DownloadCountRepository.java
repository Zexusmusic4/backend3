package com.example.zexus_music_streaming.repository;

import com.example.zexus_music_streaming.model.DownloadCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloadCountRepository extends JpaRepository<DownloadCount, Long> {
}

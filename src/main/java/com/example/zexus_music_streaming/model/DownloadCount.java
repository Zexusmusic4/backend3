package com.example.zexus_music_streaming.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class DownloadCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "music_file_id") // Foreign key in download_count table
    private MusicFile musicFile;

    private int count;

    // Default constructor
    public DownloadCount() {}
}
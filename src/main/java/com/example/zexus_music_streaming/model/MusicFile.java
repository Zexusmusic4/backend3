package com.example.zexus_music_streaming.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class MusicFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(length = 255)
    private String fileName;

    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String fileType;

    private long size;

    @NotNull
    @Size(max = 2048)
    @Column(length = 2048)
    private String url;

    @NotNull
    @Size(max = 255)
    @Column(length = 255)
    private String artistName; // Artist Name

    @NotNull
    @Size(max = 255)
    @Column(length = 255)
    private String songTitle; // Song Title

    @Column(length = 4)
    private String year; // Year of Release

    @Size(max = 255)
    @Column(length = 255)
    private String producer; // Producer

    @Size(max = 255)
    @Column(length = 255)
    private String featuring; // Featuring Artist (Optional)

    @Size(max = 255)
    @Column(length = 255)
    private String album; // Album Name

    @Size(max = 2048)
    @Column(length = 2048)
    private String artworkUrl; // URL of the Song Artwork (JPEG)

    @NotNull
    @Size(max = 255)
    @Column(length = 255)
    private String genre; // Genre of the Song (Not Null)

    @OneToOne(mappedBy = "musicFile", cascade = CascadeType.ALL)
    @JsonIgnore // Add this line
    private DownloadCount downloadCount;

    // Default constructor
    public MusicFile() {}
}
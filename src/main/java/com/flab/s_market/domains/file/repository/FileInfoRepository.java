package com.flab.s_market.domains.file.repository;

import com.flab.s_market.domains.file.domain.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

}

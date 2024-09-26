package com.service;

import com.bean.Repo;
import com.dto.ResponseCodebaseDto;
import com.dto.ResponseCommitInfoDto;
import com.dto.ResponseContributorsActivityDto;
import com.dto.ResponseIssuesDto;

import java.util.List;

public interface RepoInfoService extends BaseService {

    List<ResponseContributorsActivityDto> requestContributorsActivity(Repo repo);

    ResponseIssuesDto requestIssue(Repo repo);

    ResponseCommitInfoDto requestCommit(Repo repo);

    List<ResponseCodebaseDto> requestCodebase(Repo repo);

    Repo getRepoBy(long id);

    boolean existRepoBy(long id);
}
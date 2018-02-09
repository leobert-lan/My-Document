package com.lht.creationspace.module.proj.repository;

import com.lht.creationspace.base.domain.repository.BaseRepository;
import com.lht.creationspace.module.proj.model.ProjTypeDbModel;

/**
 * <p><b>Package:</b> com.lht.creationspace.module.proj.repository </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> ProjTypeRepository </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */

public interface ProjTypeRepository
        extends BaseRepository.NumKeyDbRepository<ProjTypeDbModel> {

    ProjTypeDbModel queryLast();
}

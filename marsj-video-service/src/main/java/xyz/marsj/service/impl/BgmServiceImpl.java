package xyz.marsj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.marsj.mapper.BgmMapper;
import xyz.marsj.pojo.Bgm;
import xyz.marsj.service.IBgmService;
@Service
public class BgmServiceImpl implements IBgmService {
	@Autowired
	private BgmMapper bgmMapper;

	@Override
	public List<Bgm> queryBgmList() {
		return bgmMapper.selectAll();
	}

	@Override
	public Bgm queryBgmById(String bgmId) {
	
		return bgmMapper.selectByPrimaryKey(bgmId);
	}
	

}

package xyz.marsj.service;

import java.util.List;

import xyz.marsj.pojo.Bgm;

public interface IBgmService {
	/**
	 * 
	 * @return 查询所有bgm
	 */
	public List<Bgm> queryBgmList();
	public Bgm queryBgmById(String bgmId);
}

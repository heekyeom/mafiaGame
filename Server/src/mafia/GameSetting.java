package mafia;

import java.util.HashMap;
import java.util.Iterator;

import comm.UserVO;

//public class GameSetting {
//	private HashMap<String,UserVO> all;
//	private HashMap<String,UserVO> citizen;
//	private HashMap<String,UserVO> mafia;
//	private HashMap<String,UserVO> dead;
//	private HashMap<String,UserVO> lives;
//	private UserVO police;
//	private UserVO doctor;
//
//	private int day_time = 120;
//	private int vote__time = 10;
//	private int argument_time = 30;
//	private int agree_time = 10;
//	private int night_time = 30;
//
//	public GameSetting() {
//	}
//	
//	public HashMap<String,UserVO> getAll() {
//		return all;
//	}
//
//	public HashMap<String,UserVO> getMafia() {
//		return mafia;
//	}
//
//	public HashMap<String,UserVO> getCitizen() {
//		return citizen;
//	}
//	
//	public HashMap<String,UserVO> getDead() {
//		return dead;
//	}
//	
//	public HashMap<String,UserVO> getLives() {
//		return lives;
//	}
//
//	public UserVO getPolice() {
//		return police;
//	}
//
//	public UserVO getDoctor() {
//		return doctor;
//	}
//
//	public int getDay_time() {
//		return day_time;
//	}
//
//	public void setDay_time(int day_time) {
//		this.day_time = day_time;
//	}
//
//	public int getVote__time() {
//		return vote__time;
//	}
//
//	public void setVote__time(int vote__time) {
//		this.vote__time = vote__time;
//	}
//
//	public int getArgument_time() {
//		return argument_time;
//	}
//
//	public void setArgument_time(int argument_time) {
//		this.argument_time = argument_time;
//	}
//
//	public int getAgree_time() {
//		return agree_time;
//	}
//
//	public void setAgree_time(int agree_time) {
//		this.agree_time = agree_time;
//	}
//
//	public int getNight_time() {
//		return night_time;
//	}
//
//	public void setNight_time(int night_time) {
//		this.night_time = night_time;
//	}
//
//	public void roleSet(int size, HashMap<String, UserVO> all) {
//
//		int count = 0;
//		int mafia = 0;
//		int police = 0;
//		int doctor = 0;
//		int[] arr = new int[size];
//
//		mafia = (int) (size * 0.3); // 마피아 수
//
//		int i = 0;
//		for (; i < mafia; i++) {
//			arr[i] = -1;
//		}
//
////		if (size >= 6) { // 경찰과 의사
////			arr[i] = 2;
////			i++;
////			arr[i] = 3;
////			i++;
////		}
//
//		for (; i < size; i++) {
//			arr[i] = 1;
//		}
//
//		i = 0;
//
//		for (; i < size; i++) {
//			int random = (int) (Math.random() * size);
//			int temp = arr[i];
//			arr[i] = arr[random];
//			arr[random] = temp;
//		}
//
//		Iterator<String> it = all.keySet().iterator();
//
//		int index = 0;
//		while (it.hasNext()) {
//			String key = it.next();
//			all.put(key, all.get(key));
//			if (arr[index] == -1) {
//				getMafia().put(key,all.get(key));
//			} else if (arr[index] == 1) {
//				getCitizen().put(key,all.get(key));
//			}
//			index++;
//		}
//	}
//}

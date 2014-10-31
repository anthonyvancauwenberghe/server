package astats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


public class DonatorsBought {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader("/Users/saosinhax/Dropbox/Reckless/donatoritemsbought.txt"));
		String line;
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		boolean old = false; //watch out
		//boolean ignore = true;
		while((line = in.readLine()) != null) {

			System.out.println(line);
			if(line.contains("//")) {
				old = false;
				continue;
			}
			if(line.startsWith("!!"))
				break;
			//System.out.println(line);
			String[] parts = line.split(";");
			int id;
			int amt;
			if(old) {
				id = Integer.parseInt(parts[1].trim());
				amt = Integer.parseInt(parts[2].trim());
			} else {
				id = Integer.parseInt(parts[2].trim());
				amt = Integer.parseInt(parts[3].trim());
			}
			if(map.containsKey(id)) {
				Integer oldamt = map.get(id);
				map.put(id, oldamt + amt);
			} else {
				map.put(id, amt);
				System.out.println("Creating id: " + id);
	            /*for(int key: map.keySet()) {
					map.put(key, 1);
				}*/
			}
		}
		in.close();
		for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
			int id = entry.getKey();
			int price = getPrice(id);
			System.out.println(id + "," + entry.getValue() + "," + price);
		}
	}

	public static int getPrice(int id) {
		switch(id) {
			case 16909:
				return 4000;
			case 19713:
			case 19714:
			case 19715:
			case 19716:
			case 19717:
			case 19718:
			case 19719:
			case 19720:
			case 19721:
				return 1999;
			case 16711:
			case 17259:
			case 16689:
			case 17361:
			case 16359:
			case 16955:
				return 1499;
			case 14484:
				return 1199;
			case 18351:
			case 18349:
			case 18353:
			case 18355:
			case 18359:
			case 18357:
				return 899;
			case 11794:
				return 999;
			case 15486:
			case 1038:
			case 1040:
			case 1042:
			case 1044:
			case 1046:
			case 1048:

				return 899;

			case 15042:
				return 1199;
			case 13740:
				return 599;
			case 15060:
			case 13738:
			case 13742:
			case 13744:
				return 199;
			case 13352:
			case 13353:
			case 13354:
			case 13355:
			case 13356:
			case 11694:
			case 15241:
			case 19143:
			case 19146:
			case 19149:
			case 1050:
			case 1053:
			case 1055:
			case 1057:
			case 1037:
			case 10887:
			case 3140:
			case 15006:
			case 15020:
				return 399;
			case 1419:
			case 10696:
			case 11698:
			case 11700:
			case 11696:
			case 19613:
			case 19615:
			case 19617:
			case 18333:
			case 18335:
			case 13736:
				return 299;
			case 11718:
			case 11720:
			case 11722:
			case 11724:
			case 11726:
			case 19459:
			case 19461:
			case 19463:
			case 19465:
				return 199;
			case 11730:
			case 15061:
			case 15062:
			case 15063:
			case 15064:
			case 15065:
			case 15066:
			case 15067:
			case 15068:
				return 150;
			case 10330:
			case 10332:
			case 10334:
			case 10336:
			case 10338:
			case 10340:
			case 10342:
			case 10344:
			case 10346:
			case 10348:
			case 10350:
			case 10352:
			case 13734:
				return 200;
			case 6889:
			case 6914:
			case 10547:
			case 10548:
			case 10549:
			case 10550:
			case 1149:
			case 4087:
			case 4585:
			case 18768:
				return 100;
			case 11728:
			case 15441:
			case 15442:
			case 15443:
			case 15444:
				return 99;
			case 4710:
			case 4718:
			case 4726:
			case 4734:
			case 4747:
			case 4755:
			case 4708:
			case 4712:
			case 4714:
			case 4716:
			case 4720:
			case 4722:
			case 4724:
			case 4728:
			case 4730:
			case 4732:
			case 4736:
			case 4738:
			case 4745:
			case 4749:
			case 4751:
			case 4753:
			case 4757:
			case 4759:
				return 79;
			case 8839:
			case 8840:
				return 75;
			case 6916:
			case 6918:
			case 6920:
			case 6922:
			case 6924:
			case 10551:
				return 50;
			case 7462:
				return 40;
			case 6570:
				return 35;
			case 11663:
			case 11664:
			case 11665:
			case 8842:
				return 30;
			case 7806:
			case 7807:
			case 8849:
			case 8850:
				return 24;
			case 7808:
			case 10499:
			case 8848:
				return 20;
			case 4508:
			case 7809:
				return 18;
			case 8847:
			case 6585:
				return 15;
			case 4509:
				return 14;
			case 4510:
			case 8846:
				return 10;
			case 4511:
			case 4566:
				return 8;
			case 4512:
				return 6;
			case 2439:
			case 8845:
				return 5;
			case 2431:
			case 2430:
			case 15332:
			case 15015:
				return 1;
			case 13883:
			case 13879:
				return 1;
			case 19152:
			case 19157:
			case 19162:
				return 1;

			default:
				return 50000;
		}

	}

	public static int getOldPrice(int id) {
		switch(id) {
			case 16909:
				return 4000;
			case 19713:
			case 19714:
			case 19715:
			case 19716:
			case 19717:
			case 19718:
			case 19719:
			case 19720:
			case 19721:
				return 1999;
			case 14484:
			case 16711:
			case 17259:
			case 16689:
			case 17361:
			case 16359:
			case 16955:
				return 1499;
			case 18351:
			case 18349:
			case 18353:
			case 18355:
			case 18359:
			case 18357:
				return 1199;
			case 11794:
				return 999;
			case 15486:
			case 1038:
			case 1040:
			case 1042:
			case 1044:
			case 1046:
			case 1048:

				return 899;

			case 15042:
				return 1199;
			case 15060:
			case 13738:
			case 13740:
			case 13742:
			case 13744:
				return 699;
			case 13352:
			case 13353:
			case 13354:
			case 13355:
			case 13356:
			case 11694:
			case 15241:
			case 19143:
			case 19146:
			case 19149:
			case 1050:
			case 1053:
			case 1055:
			case 1057:
			case 1037:
			case 10887:
			case 3140:
			case 15006:
			case 15020:
				return 399;
			case 1419:
			case 10696:
			case 11698:
			case 11700:
			case 11696:
			case 19613:
			case 19615:
			case 19617:
			case 18333:
			case 18335:
			case 13736:
				return 299;
			case 11718:
			case 11720:
			case 11722:
			case 11724:
			case 11726:
			case 19459:
			case 19461:
			case 19463:
			case 19465:
				return 199;
			case 11730:
			case 15061:
			case 15062:
			case 15063:
			case 15064:
			case 15065:
			case 15066:
			case 15067:
			case 15068:
				return 150;
			case 10330:
			case 10332:
			case 10334:
			case 10336:
			case 10338:
			case 10340:
			case 10342:
			case 10344:
			case 10346:
			case 10348:
			case 10350:
			case 10352:
			case 13734:
				return 700;
			case 6889:
			case 6914:
			case 10547:
			case 10548:
			case 10549:
			case 10550:
			case 1149:
			case 4087:
			case 4585:
			case 18768:
				return 100;
			case 11728:
			case 15441:
			case 15442:
			case 15443:
			case 15444:
				return 99;
			case 4710:
			case 4718:
			case 4726:
			case 4734:
			case 4747:
			case 4755:
			case 4708:
			case 4712:
			case 4714:
			case 4716:
			case 4720:
			case 4722:
			case 4724:
			case 4728:
			case 4730:
			case 4732:
			case 4736:
			case 4738:
			case 4745:
			case 4749:
			case 4751:
			case 4753:
			case 4757:
			case 4759:
				return 79;
			case 8839:
			case 8840:
				return 75;
			case 6916:
			case 6918:
			case 6920:
			case 6922:
			case 6924:
			case 10551:
				return 50;
			case 7462:
				return 40;
			case 6570:
				return 35;
			case 11663:
			case 11664:
			case 11665:
			case 8842:
				return 30;
			case 7806:
			case 7807:
			case 8849:
			case 8850:
				return 24;
			case 7808:
			case 10499:
			case 8848:
				return 20;
			case 4508:
			case 7809:
				return 18;
			case 8847:
			case 6585:
				return 15;
			case 4509:
				return 14;
			case 4510:
			case 8846:
				return 10;
			case 4511:
			case 4566:
				return 8;
			case 4512:
				return 6;
			case 2439:
			case 8845:
				return 5;
			case 2431:
			case 2430:
			case 15332:
			case 15015:
				return 1;
			case 13883:
			case 13879:
				return 2;
			case 19152:
			case 19157:
			case 19162:
				return 2;

		}
		return 50000;
	}
}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class crc {
	private static ArrayList<Integer> value_read;
	private static ArrayList<String> revise_value;
	private static int for_crc;
	private static int print_index;
	private static String concated_all;
	private static int id=17;
	
	private static String concat_all(ArrayList<String> arraylist) {
		String temp = "";
		for(int i = 0; i < arraylist.size(); i++) {
			temp = temp.concat(arraylist.get(i));
		}
		return temp;
	}
	
	private static String hex_to_bin_include_zero(String bin, String value, int bit) {
		int start = 0;
		if(bit == 7) {
			start = 25;
		} else {
			start = 16;
		}
		for (int j = start; j<Integer.numberOfLeadingZeros(Integer.parseInt(value, 16)); j++) {
			bin += "0";
		}
		bin += Integer.toBinaryString(Integer.parseInt(value, 16));
		return bin;
	}
	
	private static int one_count_for_bin_8bit(String bin, int count) {
		for (int q = 0;q<bin.length();q++) {
			if(bin.charAt(q)=='1') {
				count ++;
			}
		}
		return count;
	}
	
	private static String make_bin_8bit_added_parity(String bin_8bit, String bin_7bit, int count, int i) {
		if(count%2 == 0) {
			bin_8bit = bin_7bit+"0";
		} else {
			bin_8bit = bin_7bit+"1";
		}
		return bin_8bit;
	}
	
	private static int[] Strbin_to_binarr(String Strbin, int[] arraybin) {
		for(int i = 0;i < Strbin.length(); i++) {
			int temp_one = Integer.parseInt(Strbin.substring(i,i+1));
			arraybin[i] = temp_one;
		}
		return arraybin;
	}
	
	private static int[] binarr_shift(int[] array, int[] for_chiper_array) {
		int temp_index = 0;
		int count = 0;
		for(int q = 0; q < array.length; q++) {
			if(array[q] == 1) {
				temp_index = q;
				count++;
				break;
			} else {
				count++;
			}	
			if(count == 17) {
				temp_index = 17;
				count = 0;
				break;
			}
		}
		
		int temp_value = 0;	
		int temp_id = 0;
		for_crc += temp_index;
		if(for_crc > concated_all.length()-1) {
			temp_id = id;
			
			while(true) {
				id++;
				if(id == concated_all.length() + 16) {
					id = temp_id;
					temp_index = Math.abs(id-(concated_all.length()+16));
					break;
				}
			}
			
			temp_value = 0;
			for(int k = 0; k < array.length-temp_index; k++) {
				temp_value = array[k+temp_index];
				array[k] = temp_value;
			}
			for(int u = array.length-temp_index; u < 17; u++, id++) {
				if(id == concated_all.length()+16) {
					break;
				}
				array[u] = for_chiper_array[id];
			}
		} else {
			temp_value = 0;
			for(int k = 0; k < array.length-temp_index; k++) {
				temp_value = array[k+temp_index];
				array[k] = temp_value;
			}
			
			print_index = array.length;
			for(int u = array.length-temp_index; u < print_index ; u++, id++) {
				array[u] = for_chiper_array[id];
			}
		}
		return array;
	}
	
	
	private static int[] xor(int[] temp, int[] D) {
		for(int p = 0; p < D.length; p++) {
			temp[p] = temp[p] ^ D[p];
		}
		return temp;
	}
	
	public static void main(String[] args) {
		value_read = new ArrayList<Integer>();
		revise_value = new ArrayList<String>();
		
		try {
			File file_obj = new File("./input.txt");			
			FileReader file_reader = new FileReader(file_obj);
			BufferedReader buffer_read = new BufferedReader(file_reader);
			
			int read = 0;
			
			while((read = buffer_read.read()) != -1) {
				value_read.add(read);
			}
			buffer_read.close();
		} catch (FileNotFoundException e) {
			System.out.println("Please check the file path.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		if(value_read.isEmpty()) {
			System.out.println("Please input the data.");
			System.exit(0);
		}

		for(int i = 0;i<value_read.size();i++) {
			String num = Integer.toHexString(value_read.get(i));
			
			String bin_7bit = "";
			bin_7bit = hex_to_bin_include_zero(bin_7bit, num, 7);  //16���� -> 7��Ʈ 2����
			
			int count = 0;
			count = one_count_for_bin_8bit(bin_7bit, count);  //7��Ʈ 2������ 1���� ����
			
			String bin_8bit_include_parity = "";
			bin_8bit_include_parity = make_bin_8bit_added_parity(bin_8bit_include_parity, bin_7bit, count, i); //1������ ���� �и�Ƽ��Ʈ �߰��ϱ� 8��Ʈ 2���� ����
			
			revise_value.add(bin_8bit_include_parity);
		}
		concated_all = concat_all(revise_value);
		
		int[] G = new int[concated_all.length()];
		String Divisor_CRC16_IBM = "11000000000000101";
		int[] D = new int[17];
		
		D = Strbin_to_binarr(Divisor_CRC16_IBM, D);
		G = Strbin_to_binarr(concated_all, G);
		int[] G_mul_Dsub1 = Arrays.copyOf(G, G.length + D.length - 1);	
		int[] cal = Arrays.copyOf(G_mul_Dsub1, 17);

		for_crc = 0;
		while(true) {
			cal = binarr_shift(cal, G_mul_Dsub1);

			if(for_crc == concated_all.length()-1) {
				cal = xor(cal, D);
				break;
			}
			else if(for_crc > concated_all.length()-1) {
				break;
			}
			
			cal = xor(cal, D);
		}
		String CRC = "";
		System.out.print("Remainder: ");
		for(int w = 1; w < 17; w++) {
			System.out.print(cal[w]);
			CRC = CRC.concat(Integer.toString(cal[w]));
		}
		try {
			File file = new File("./output_crc.txt");
			FileWriter fw = new FileWriter(file, false);
			
			fw.write(CRC);
			fw.flush();
			
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class checksum {

	private static ArrayList<Integer> value_read;
	private static ArrayList<String> revise_value_16bit;
	
	
	private static String concat_16bit(int i, String temp, String value_8bit) {
		if (i%2==0) {
			temp = value_8bit;	
			i++;
		} else {
			temp = temp.concat(value_8bit);
			revise_value_16bit.add(temp);
			i=0;
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
	
	private static String[] ones_complement(String[] array) {
		for(int i = 0;i < 16; i++) {
			if(Integer.parseInt(array[i]) == 0) {
				array[i] = "1";
			} 
			else {
				array[i] = "0";
			}
		}
		return array;
	}
	
	private static String[] Strbin_to_arraybin(String Strbin, String[] arraybin) {
		for(int i = 0;i < Strbin.length(); i++) {
			String temp_one = Strbin.substring(i,i+1);
			arraybin[i] = temp_one;
		}
		return arraybin;
	}
	
	public static void main(String[] args) {
		value_read = new ArrayList<Integer>();
		revise_value_16bit = new ArrayList<String>();
		
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

		int sum = 0;
		String temp = "";
		
		for(int i = 0;i<value_read.size();i++) {
			String num = Integer.toHexString(value_read.get(i));
			
			String bin_7bit = "";	
			int count = 0;
			bin_7bit = hex_to_bin_include_zero(bin_7bit, num, 7);	//16���� -> 7��Ʈ 2����
			
			count = one_count_for_bin_8bit(bin_7bit, count);	//7��Ʈ 2������ 1���� ����
			
			String bin_8bit_include_parity = "";
			bin_8bit_include_parity = make_bin_8bit_added_parity(bin_8bit_include_parity, bin_7bit, count, i); //1������ ���� �и�Ƽ��Ʈ �߰��ϱ� 8��Ʈ 2���� ����

			temp = concat_16bit(i, temp, bin_8bit_include_parity);	//8��Ʈ 2���� ��ġ��-> 16��Ʈ 2���� 

		}
		
		int decimal = 0;
		if(revise_value_16bit.isEmpty()) {
			System.out.println("Please input the data. bigger than 16bit.");
			System.exit(0);
		}
		
		for(int i = 0; i<revise_value_16bit.size();i++) {
			decimal = Integer.parseInt(revise_value_16bit.get(i),2);
			sum += decimal;
			
		}
		String num = Integer.toHexString(sum);	
		int hex_num = 0;
		String carry = "";
		String hex_str = "";
		int final_num = 0;
		if(num.length()>4) {
			carry = num.substring(0, num.length()-4);
			num = num.substring(num.length()-4, num.length());
			int hex_carry = Integer.decode("0x"+carry);
			hex_num = Integer.decode("0x"+num);
			hex_num += hex_carry;
			hex_str = Integer.toHexString(hex_num);
			if(hex_str.length()>4) {
				carry = hex_str.substring(0, hex_str.length()-4);
				hex_str = hex_str.substring(hex_str.length()-4, hex_str.length());
				hex_carry = Integer.decode("0x"+carry);
				hex_num = Integer.decode("0x"+hex_str);
				hex_num += hex_carry;
				hex_str = Integer.toHexString(hex_num);
			}
			
			final_num = Integer.parseInt(hex_str, 16);
			
		} else {
			final_num = Integer.parseInt(num, 16);	//ĳ�� X ó��
		}
		
		String checksum_0x = Integer.toHexString(final_num);
		String checksum_bin = "";

		checksum_bin = hex_to_bin_include_zero(checksum_bin, checksum_0x, 16);	
		
		String[] checksum_array = new String[16];
		checksum_array = Strbin_to_arraybin(checksum_bin, checksum_array);
		
		
		String[] final_checksum = ones_complement(checksum_array);
		
		String Checksum = "";
		System.out.print("Checksum: ");
		for(int i = 0; i < 16; i++){
			System.out.print(final_checksum[i]);
			Checksum = Checksum.concat(final_checksum[i]);
		}
		try {
			File file = new File("./output_checksum.txt");
			FileWriter fw = new FileWriter(file, false);
			
			fw.write(Checksum);
			fw.flush();
			
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
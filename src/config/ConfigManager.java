package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ConfigManager
{
	public static final String NO_FILE_CHOSEN = "No File Chosen!";

	private ArrayList<String> foundFileNames;
	private ArrayList<String> allFileNames;

	private File directoryFile;
	private String directoryString;
	private Path directoryPath;

	private DirectoryStream<Path> fileListStream;

	public int fileFoundCount;
	
	public ConfigManager(File directoryFile)
	{
		this.directoryFile = directoryFile;
		this.directoryString = directoryFile.getPath();
	}
	
	public String getDirectoryString()
	{
		return this.directoryString;
	}

	public File getDirectoryFile()
	{
		return directoryFile;
	}

	public void setDirectoryFile(File directoryFile)
	{
		this.directoryFile = directoryFile;
		this.directoryString = directoryFile.getPath();
	}

	public void resetSearch() throws IOException
	{
		this.directoryPath = FileSystems.getDefault().getPath(this.directoryString);
		this.fileListStream = Files.newDirectoryStream(this.directoryPath);

		this.allFileNames = new ArrayList<String>();
		this.foundFileNames = new ArrayList<String>();
	}

	public ConfigItem[] search(String searchWord, Boolean caseSensitive) throws IOException
	{
		resetSearch();

		ArrayList<ConfigItem> foundList = new ArrayList<ConfigItem>();
		
		int fileCount = 0;
		int foundCount = 0;

		for(Path path : this.fileListStream)
		{
			int lineCount = 1;
			String fileName = path.getFileName().toString();
			this.allFileNames.add(fileName);
			if(!fileName.startsWith(".") && !fileName.startsWith("report"))
			{
				BufferedReader br = Files.newBufferedReader(path, Charset.defaultCharset());
				String line;

				while((line = br.readLine()) != null)
				{
					if(!caseSensitive)
					{
						if (line.toLowerCase().indexOf(searchWord.toLowerCase()) >= 0)
						{
							ConfigItem item = new ConfigItem(++foundCount, fileName, ++lineCount, line);
							System.out.println(item.toString());
							foundList.add(item);
							if (!foundFileNames.contains(item.getFileName()))
								this.foundFileNames.add(item.getFileName());
						}
					}
					else
					{
						if(line.indexOf(searchWord) >= 0)
						{
							ConfigItem item = new ConfigItem(++foundCount, fileName, ++lineCount, line);
							System.out.println(item.toString());
							foundList.add(item);
							if (!foundFileNames.contains(item.getFileName()))
								this.foundFileNames.add(item.getFileName());
						}

					}
				}
				fileCount++;
				br.close();
			}
		}
		
		System.out.println("File Count: " + fileCount);
		this.fileFoundCount = fileCount;
		
		ConfigItem listArray[] = new ConfigItem[foundList.size()];

		if (listArray.length == 0)
			return null;

		foundList.toArray(listArray);

		return listArray;
	}

	public ConfigItem[] getNotFoundItems()
	{
		ArrayList<String> items = new ArrayList<String>(this.allFileNames);

		for (int i = 0; i < foundFileNames.size(); i++)
		{
			String fileName = foundFileNames.get(i);

			if(items.contains(fileName))
				items.remove(fileName);
		}

		if(items.contains("report.xls"))
			items.remove("report.xls");

		ConfigItem[] arr = new ConfigItem[items.size()];

		for (int i = 0; i < items.size(); i++)
		{
			ConfigItem item = new ConfigItem(i+1, items.get(i));
			arr[i] = item;
		}

		return arr;
	}

	public String[] getFoundItems()
	{
		String[] arr = new String[this.foundFileNames.size()];

		this.foundFileNames.toArray(arr);

		return arr;
	}
}



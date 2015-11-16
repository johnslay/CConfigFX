package config;

public class ConfigItem
{
	private int id;
	private String fileName;
	private int lineNumber;
	private String lineCommand;

	public ConfigItem(int id, String fileName)
	{
		this.id = id;
		this.fileName = fileName;
	}
	
	public ConfigItem(int id, String fileName, int lineNumber, String lineCommand)
	{
		this.id = id;
		this.setFileName(fileName);
		this.setLineNumber(lineNumber);
		this.lineCommand = lineCommand;
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	public String getLineCommand()
	{
		return lineCommand;
	}

	public void setLineCommand(String lineCommand)
	{
		this.lineCommand = lineCommand;
	}
	
	public String toString()
	{
		return "ID: " + id + "         File: " + fileName + "       \tLine: " + lineNumber + "       \tCommand: " + lineCommand;
	}
}

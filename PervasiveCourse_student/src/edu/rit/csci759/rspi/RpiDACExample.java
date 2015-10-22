package edu.rit.csci759.rspi;

import edu.rit.csci759.rspi.utils.AdafruitMCP4725;



public class RpiDACExample {

  public static void main(String[] args)// throws IOException
  {
    System.out.println("The output happens on the VOUT terminal of the MCP4725.");
    AdafruitMCP4725 dac = new AdafruitMCP4725();

    for (int i=0; i<5; i++)
    {
      for (int volt : AdafruitMCP4725.DACLookupFullSine9Bit)
      {
        dac.setVoltage(volt);
        try { Thread.sleep(10L); } catch (InterruptedException ie) {}
      }
    }
  }
  }
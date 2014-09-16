package wci.message;

import static wci.message.MessageType.*;

import wci.frontend.*;
import wci.frontend.java.JavaTokenType;

/**
 * <h1>Message</h1>
 *
 * <p>
 * Message format.</p>
 *
 * <p>
 * Copyright (c) 2009 by Ronald Mak</p>
 * <p>
 * For instructional purposes only. No warranties.</p>
 */
public class Message
 {
  private MessageType type;
  private Object body;

  /**
   * Constructor.
   *
   * @param type the message type.
   * @param body the message body.
   */
  public Message(MessageType type, Object body)
   {
    this.type = type;
    this.body = body;
   }

  /**
   * Getter.
   *
   * @return the message type.
   */
  public MessageType getType()
   {
    return type;
   }

  /**
   * Getter.
   *
   * @return the message body.
   */
  public Object getBody()
   {
    return body;
   }

  @Override
  public String toString()
   {
    String s = "";
    Object[] o = (Object[]) body;
    switch (type)
     {
      case SOURCE_LINE:
        s += (Integer) o[0];
        s = padString(s);

        s += " " + (String) o[1];
        break;
        
      case TOKEN:
        s += ">>> ";
        s += ((TokenType)o[2]).toString();
        String temp = padString(((Integer)o[0]).toString());
        s += "\tline: " + temp;
        temp = padString(((Integer)o[1]).toString());
        s += "\tpos: " + temp;
        s += "\ttext: " + ((String) o[3]);
        //if (o.length > 3 && o[4] != null) { s += "\tvalue: " + (o[4].toString()); }
          // For testing the value of tokens that have one
        break;

      case SYNTAX_ERROR:
        s += "*** ";
        s += (String)o[3];
        s += " at " + padString(((Integer)o[0]).toString());
        
      default:
     }

    return s;
   }
  
  private String padString(String str)
   {
    String s = str;
    while (s.length() < 3) //pad with 0's at start
         {
          s = "0" + s;
         }
    return s;
   }
 }

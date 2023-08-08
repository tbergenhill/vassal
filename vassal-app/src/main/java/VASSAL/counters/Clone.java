/*
 *
 * Copyright (c) 2003 by Rodney Kinney
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */

package VASSAL.counters;

import VASSAL.build.GameModule;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.command.AddPiece;
import VASSAL.command.Command;
import VASSAL.configure.NamedHotKeyConfigurer;
import VASSAL.configure.StringConfigurer;
import VASSAL.i18n.PieceI18nData;
import VASSAL.i18n.Resources;
import VASSAL.i18n.TranslatablePiece;
import VASSAL.property.PersistentPropertyContainer;
import VASSAL.tools.NamedKeyStroke;
import VASSAL.tools.SequenceEncoder;

import javax.swing.KeyStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This trait adds a command that creates a duplicate of the selected Gamepiece
 */
public class Clone extends Decorator implements TranslatablePiece {
  public static final String ID = "clone;"; // NON-NLS
  public static final String CLONE_ID = "CloneID";

  protected KeyCommand[] command;
  protected String commandName;
  protected NamedKeyStroke key;
  protected KeyCommand cloneCommand;
  protected String description = "";

  public Clone() {
    this(ID + Resources.getString("Editor.Clone.clone") + ";C", null); // NON-NLS
  }

  public Clone(String type, GamePiece inner) {
    mySetType(type);
    setInner(inner);
  }

  @Override
  public void mySetType(String type) {
    type = type.substring(ID.length());
    final SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(type, ';');
    commandName = st.nextToken();
    key = st.nextNamedKeyStroke('C');
    description = st.nextToken("");
    command = null;
  }

  @Override
  public String myGetType() {
    final SequenceEncoder se = new SequenceEncoder(';');
    se.append(commandName).append(key).append(description);
    return ID + se.getValue();
  }

  @Override
  protected KeyCommand[] myGetKeyCommands() {
    if (command == null) {
      cloneCommand = new KeyCommand(commandName, key, Decorator.getOutermost(this), this);
      if (commandName.length() > 0 && key != null && ! key.isNull()) {
        command =
            new KeyCommand[]{cloneCommand};
      }
      else {
        command = KeyCommand.NONE;
      }
    }
    if (command.length > 0) {
      command[0].setEnabled(getMap() != null);
    }
    return command;
  }

  @Override
  public String myGetState() {
    return "";
  }

  @Override
  public Command myKeyEvent(KeyStroke stroke) {
    Command c = null;
    myGetKeyCommands();
    if (cloneCommand.matches(stroke)) {
      final GamePiece outer = Decorator.getOutermost(this);
      final GamePiece newPiece = ((AddPiece) GameModule.getGameModule().decode(GameModule.getGameModule().encode(new AddPiece(outer)))).getTarget();
      newPiece.setId(null);
      GameModule.getGameModule().getGameState().addPiece(newPiece);
      newPiece.setState(outer.getState());
      c = new AddPiece(newPiece);

      // Set the UniqueID and CloneID
      if (newPiece instanceof PersistentPropertyContainer) {

        // Set the UniqueId in the target piece to its current PieceUID
        c = c.append(((PersistentPropertyContainer) newPiece).setPersistentProperty(BasicPiece.UNIQUE_ID, newPiece.getProperty(BasicPiece.PIECE_UID)));

        // Find the CloneID for this piece
        String cloneId = (String) getProperty(CLONE_ID);
        if (cloneId == null || cloneId.isEmpty()) {
          // Never been cloned and not a clone, set a new CloneID
          cloneId = (String) getProperty(BasicPiece.UNIQUE_ID);
          c = c.append(((PersistentPropertyContainer) this).setPersistentProperty(CLONE_ID, cloneId));
        }

        // Set the CloneID into the target piece
        c = c.append(((PersistentPropertyContainer) newPiece).setPersistentProperty(CLONE_ID, cloneId));

      }

      if (getMap() != null) {
        c.append(getMap().placeOrMerge(newPiece, outer.getPosition()));
        KeyBuffer.getBuffer().remove(outer);
        KeyBuffer.getBuffer().add(newPiece);

        // Handles any auto-attachment traits in newly created pieces
        c = c.append(GameModule.getGameModule().getGameState().getAttachmentManager().doAutoAttachments());
      }
    }
    return c;
  }

  @Override
  public void mySetState(String newState) {
  }

  @Override
  public Rectangle boundingBox() {
    return piece.boundingBox();
  }

  @Override
  public void draw(Graphics g, int x, int y, Component obs, double zoom) {
    piece.draw(g, x, y, obs, zoom);
  }

  @Override
  public String getName() {
    return piece.getName();
  }

  @Override
  public Shape getShape() {
    return piece.getShape();
  }

  @Override
  public PieceEditor getEditor() {
    return new Ed(this);
  }

  @Override
  public String getDescription() {
    String s = buildDescription("Editor.Clone.trait_description", description);
    s += getCommandDesc(commandName, key);
    return s;
  }

  @Override
  public String getBaseDescription() {
    return Resources.getString("Editor.Clone.trait_description");
  }

  @Override
  public String getDescriptionField() {
    return description;
  }

  /**
   * @return a list of any Named KeyStrokes referenced in the Decorator, if any (for search)
   */
  @Override
  public List<NamedKeyStroke> getNamedKeyStrokeList() {
    return Arrays.asList(key);
  }

  /**
   * @return a list of any Menu Text strings referenced in the Decorator, if any (for search)
   */
  @Override
  public List<String> getMenuTextList() {
    return List.of(commandName);
  }


  @Override
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("GamePiece.html", "Clone"); // NON-NLS
  }

  @Override
  public PieceI18nData getI18nData() {
    return getI18nData(commandName, Resources.getString("Editor.Clone.clone_command_description"));
  }

  @Override
  public List<String> getPropertyNames() {
    final ArrayList<String> l = new ArrayList<>();
    l.add(CLONE_ID);
    return l;
  }

  @Override
  public boolean testEquals(Object o) {
    if (! (o instanceof Clone)) return false;
    final Clone c = (Clone) o;
    if (! Objects.equals(commandName, c.commandName)) return false;
    return Objects.equals(key, c.key);
  }

  public static class Ed implements PieceEditor {
    private final StringConfigurer nameInput;
    private final NamedHotKeyConfigurer keyInput;
    private final TraitConfigPanel controls;
    private final StringConfigurer descInput;

    public Ed(Clone p) {
      controls = new TraitConfigPanel();

      descInput = new StringConfigurer(p.description);
      descInput.setHintKey("Editor.description_hint");
      controls.add("Editor.description_label", descInput);

      nameInput = new StringConfigurer(p.commandName);
      nameInput.setHintKey("Editor.menu_command_hint");
      controls.add("Editor.menu_command", nameInput);

      keyInput = new NamedHotKeyConfigurer(p.key);
      controls.add("Editor.keyboard_command", keyInput);

    }

    @Override
    public Component getControls() {
      return controls;
    }

    @Override
    public String getType() {
      final SequenceEncoder se = new SequenceEncoder(';');
      se.append(nameInput.getValueString()).append(keyInput.getValueString()).append(descInput.getValueString());
      return ID + se.getValue();
    }

    @Override
    public String getState() {
      return "";
    }
  }
}

package VASSAL.tools.imageop;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import VASSAL.tools.HashCode;

public class OrthoRotateOp extends AbstractTiledOp {
  private final ImageOp sop;
  private final int angle;
  private final int hash;

  public OrthoRotateOp(ImageOp sop, int angle) {
    if (sop == null) throw new IllegalArgumentException();

    angle = (360 + (angle % 360)) % 360;  // put angle in [0,360)
    if (angle % 90 != 0) throw new IllegalArgumentException();
 
    // angle is now in { 0, 90, 180, 270 }.

    this.sop = sop;
    this.angle = angle / 90;

    hash = HashCode.hash(sop) ^ HashCode.hash(angle);
  }

  protected Image apply() throws Exception {
    if (size == null) fixSize();

    final BufferedImage dst =
      new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);

    final Graphics2D g = dst.createGraphics();
    g.rotate(Math.PI/2.0*angle, sop.getWidth()/2.0, sop.getHeight()/2.0);
    g.drawImage(sop.getImage(null), 0, 0, null);
    g.dispose();

    return dst;
  }

  protected void fixSize() {
    if ((size = getSizeFromCache()) == null) {
      size = sop.getSize();

      // transpose dimensions for 90- and 270-degree rotations
      if (angle == 1 || angle == 3) 
        size.setSize(size.height, size.width);
    }

    tileSize = new Dimension(256,256);

    numXTiles = (int) Math.ceil((double)size.width/tileSize.width);
    numYTiles = (int) Math.ceil((double)size.height/tileSize.height);

    tiles = new ImageOp[numXTiles*numYTiles];
  }

  public int getAngle() {
    return angle * 90;
  }

  protected ImageOp getTileOp(int tileX, int tileY) {
    ImageOp top = tiles[tileY*numXTiles + tileX];
    if (top == null) {
      top = tiles[tileY*numXTiles + tileX] =
        new TileOp(this, tileX, tileY);
    }
  
    return top;
  }

  private static class TileOp extends AbstractTileOp {
    private final ImageOp sop;
    private final int angle;
    private final int hash;
 
    public TileOp(OrthoRotateOp rop, int tileX, int tileY) {
      if (rop == null) throw new IllegalArgumentException();

      if (tileX < 0 || tileX >= rop.getNumXTiles() ||
          tileY < 0 || tileY >= rop.getNumYTiles())
        throw new IndexOutOfBoundsException(); 

      this.angle = rop.angle;

      final int sx0, sy0, sx1, sy1;

      switch (angle) {
      case 0:
        sx0 = tileX*rop.tileSize.width;
        sy0 = tileY*rop.tileSize.height;
        sx1 = Math.min((tileX+1)*rop.tileSize.width, rop.size.width);
        sy1 = Math.min((tileY+1)*rop.tileSize.height, rop.size.height);
        break;
      case 1:
        // FIXME: test this
        sx0 = tileY*rop.tileSize.height;
        sy0 = tileX*rop.tileSize.width;
        sx1 = Math.min((tileY+1)*rop.tileSize.height, rop.size.height);
        sy1 = Math.min((tileX+1)*rop.tileSize.width, rop.size.width);
        break;
      case 2:
        sx1 = rop.size.width - tileX*rop.tileSize.width - 1;
        sy1 = rop.size.height - tileY*rop.tileSize.height - 1;
        sx0 = rop.size.width -
                Math.min((tileX+1)*rop.tileSize.width, rop.size.width) -1;
        sy0 = rop.size.height -
                Math.min((tileY+1)*rop.tileSize.height, rop.size.height) - 1;
        break;
      case 3:
      default:
        // FIXME: test this
        sx1 = rop.size.height - tileY*rop.tileSize.height - 1;
        sy1 = rop.size.width - tileX*rop.tileSize.width - 1;
        sx0 = rop.size.height -
                Math.min((tileY+1)*rop.tileSize.height, rop.size.height) - 1;
        sy0 = rop.size.width -
                Math.min((tileX+1)*rop.tileSize.width, rop.size.width) -1;
        break;
      }

      size = new Dimension(sx1-sx0, sy1-sy0);

      this.sop = new CropOp(rop.sop, sx0, sy0, sx1, sy1);
                            
      hash = HashCode.hash(sop) ^ HashCode.hash(angle);
    }

    protected Image apply() throws Exception {
      final BufferedImage dst =
        new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);

      final Graphics2D g = dst.createGraphics();
      g.rotate(Math.PI/2.0*angle, sop.getWidth()/2.0, sop.getHeight()/2.0);
      g.drawImage(sop.getImage(null), 0, 0, null);
      g.dispose();

      return dst; 
    }

    protected void fixSize() { }
   
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || !(o instanceof TileOp)) return false;

      final TileOp op = (TileOp) o;
      return angle == op.angle &&
             sop.equals(op.sop);
    }

    @Override
    public int hashCode() {
      return hash;
    }
  } 

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || !(o instanceof OrthoRotateOp)) return false;
    
    final OrthoRotateOp op = (OrthoRotateOp) o;
    return angle == op.angle && sop.equals(op.sop);
  }

  @Override
  public int hashCode() {
    return hash;
  }
}

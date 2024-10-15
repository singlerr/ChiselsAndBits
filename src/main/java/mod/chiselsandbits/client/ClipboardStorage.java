package mod.chiselsandbits.client;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.nbt.CompoundTag;

public class ClipboardStorage {

  public ClipboardStorage(final File file) {
  }

  public void write(final List<CompoundTag> myitems) {
        /*if ( !ChiselsAndBits.getConfig().getServer().persistCreativeClipboard.get() )
        {
        	return;
        }

        for ( final String name : getCategoryNames() )
        {
        	removeCategory( getCategory( name ) );
        }

        int idx = 0;
        for ( final NBTTagCompound nbt : myitems )
        {
        	final PacketBuffer b = new PacketBuffer( Unpooled.buffer() );
        	b.writeNBTTagCompoundToBuffer( nbt );

        	final int[] o = new int[b.writerIndex()];
        	for ( int x = 0; x < b.writerIndex(); x++ )
        	{
        		o[x] = b.getByte( x );
        	}

        	get( "clipboard", "" + idx++, o ).set( o );
        }

        save();*/
  }

  public List<CompoundTag> read() {
        /*final List<NBTTagCompound> myItems = new ArrayList<NBTTagCompound>();

        if ( !ChiselsAndBits.getConfig().getServer().persistCreativeClipboard.get() )
        {
        	return myItems;
        }

        for ( final Property p : getCategory( "clipboard" ).values() )
        {
        	final int[] bytes = p.getIntList();
        	final byte[] o = new byte[bytes.length];

        	for ( int x = 0; x < bytes.length; x++ )
        	{
        		o[x] = (byte) bytes[x];
        	}

        	try
        	{
        		final PacketBuffer b = new PacketBuffer( Unpooled.wrappedBuffer( o ) );
        		final NBTTagCompound c = b.readNBTTagCompoundFromBuffer();

        		myItems.add( c );
        	}
        	catch ( final EncoderException e )
        	{
        		// :_ (
        	}
        	catch ( final EOFException e )
        	{
        		// :_ (
        	}
        	catch ( final IOException e )
        	{
        		// :_ (
        	}

        }

        return myItems;*/
    return Lists.newArrayList();
  }
}

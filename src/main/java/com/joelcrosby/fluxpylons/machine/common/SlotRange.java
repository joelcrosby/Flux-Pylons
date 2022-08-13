package com.joelcrosby.fluxpylons.machine.common;

public class SlotRange
{
    private final int low;
    private final int high;

    private final int[] values;
    
    public SlotRange(int low, int high){
        this.low = low;
        this.high = high;
        
        var values = new int[high - low + 1];
        
        var i = 0;
        for (var v = low; v <= high; v++) {
            values[i] = v;
            i ++;
        }
        
        this.values = values;
    }

    public boolean contains(int number) {
        return (number >= low && number <= high);
    }
    
    public int[] values() {
        return values;
    }
}

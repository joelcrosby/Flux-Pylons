package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.item.upgrade.extract.UpgradeExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.UpgradeFluidExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.UpgradeFilterItem;

import java.util.List;

public record PipeUpgrades(List<UpgradeExtractItem> items, List<UpgradeFluidExtractItem> fluids, List<UpgradeFilterItem> filters) {
}

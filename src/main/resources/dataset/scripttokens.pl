while(<STDIN>) {
    chomp;
    @fields = split (" ",$_);
    print "$fields[2],\t$fields[3],\t$fields[4]\n"
}
package in.co.tripin.chai_tapri_app.POJOs.Responces;

public class HubItemsPojo {
    private String status;

    private Data data;

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [status = "+status+", data = "+data+"]";
    }

    public class Data
    {
        private Item[] Chaihiyeh;

        private Item[] Extra;

        private Item[] Beverages;

        private Item[] Snacks;

        public Item[] getChaihiyeh ()
        {
            return Chaihiyeh;
        }

        public void setChaihiyeh (Item[] Chaihiyeh)
        {
            this.Chaihiyeh = Chaihiyeh;
        }

        public Item[] getExtra ()
        {
            return Extra;
        }

        public void setExtra (Item[] Extra)
        {
            this.Extra = Extra;
        }

        public Item[] getBeverages ()
        {
            return Beverages;
        }

        public void setBeverages (Item[] Beverages)
        {
            this.Beverages = Beverages;
        }

        public Item[] getSnacks ()
        {
            return Snacks;
        }

        public void setSnacks (Item[] Snacks)
        {
            this.Snacks = Snacks;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [Chaihiyeh = "+Chaihiyeh+", Extra = "+Extra+", Beverages = "+Beverages+", Snacks = "+Snacks+"]";
        }

        public class Item
        {
            private String category;

            private String flag;

            private String rate;

            private String _id;

            private String status;

            private String name;

            private int quantity = 0;

            public String getFlag() {
                return flag;
            }

            public void setFlag(String flag) {
                this.flag = flag;
            }

            public String getCategory ()
            {
                return category;
            }

            public void setCategory (String category)
            {
                this.category = category;
            }

            public String getRate ()
            {
                return rate;
            }

            public void setRate (String rate)
            {
                this.rate = rate;
            }

            public String get_id ()
            {
                return _id;
            }

            public void set_id (String _id)
            {
                this._id = _id;
            }

            public String getStatus ()
            {
                return status;
            }

            public void setStatus (String status)
            {
                this.status = status;
            }

            public String getName ()
            {
                return name;
            }

            public void setName (String name)
            {
                this.name = name;
            }

            @Override
            public String toString()
            {
                return "ClassPojo [category = "+category+", rate = "+rate+", _id = "+_id+", status = "+status+", name = "+name+"]";
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }
        }
    }
}

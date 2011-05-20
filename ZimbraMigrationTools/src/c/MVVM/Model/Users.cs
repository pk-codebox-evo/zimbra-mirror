﻿
namespace MVVM.Model
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    using System.Linq;

    public class Users
    {
        public Users() { }
        internal Users(string usernameEntered, string username, string mappedname, int currentUserSelection)
        {
            this.UsernameEntered = usernameEntered;
            this.Username = username;
            this.MappedName = mappedname;
            this.CurrentUserSelection = currentUserSelection;
        }

        public string UsernameEntered
        {
            get; set;
        }

        public string Username
        {
            get; set;
        }

        public string MappedName
        {
            get; set;
        }

        public int CurrentUserSelection
        {
            get; set;
        }

        public bool MinusEnabled
        {
            get; set;
        }
        public static string ToCsv<T>(string separator, IEnumerable<T> objectlist)
        {
            Type t = typeof(T);
            System.Reflection.FieldInfo[] fields = t.GetFields();
            string header = String.Join(separator, fields.Select(f => f.Name).ToArray());
            StringBuilder csvdata = new StringBuilder();
            csvdata.AppendLine(header);
            foreach (var o in objectlist)
                csvdata.AppendLine(ToCsvFields(separator, fields, o));
            return csvdata.ToString();
        }

        public static string ToCsvFields(string separator, System.Reflection.FieldInfo[] fields, object o)
        {
            StringBuilder linie = new StringBuilder();
            foreach (var f in fields)
            {
                if (linie.Length > 0)
                    linie.Append(separator); var x = f.GetValue(o);
                if (x != null)
                    linie.Append(x.ToString());
            }
            return linie.ToString();
        }

        public bool ChangePWD;
        public string PWDdefault;

    }
}

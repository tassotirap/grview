package org.grview.project;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
	 * Info about version of any file. A project may contain 
	 * many different versions for each file;
	 *
	 */
	public class Version implements Comparator, Cloneable, Serializable {
		
		private String versionName;
		private String description;
		private Date creationDate;
		private Date modDate;
		
		/**
		 * Compares two version, if thisVersion comes first then the method
		 * returns -1, if it comes after the method returns 1, otherwise they're the
		 * same and the method returns 0.
		 */
		public int compare(Object thisVersion, Object otherVersion) {
			assert (thisVersion instanceof Version) && (otherVersion instanceof Version);
			if (((Version)thisVersion).getCreationDate().before(((Version)otherVersion).getCreationDate())) {
				return 1;
			}
			else if (((Version)thisVersion).getCreationDate().after(((Version)otherVersion).getCreationDate())){
				return -1;
			}
			return 0;
		}

		@Override
		public Version clone() {
			Object clone = null;  
			try {  
				clone = super.clone();  
			} catch (CloneNotSupportedException ex) {  
				ex.printStackTrace();  
			}  
			return (Version) clone;  
		}
		
		public String getVersionName() {
			return versionName;
		}

		public void setVersionName(String versionName) {
			this.versionName = versionName;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Date getCreationDate() {
			return creationDate;
		}

		public void setCreationDate(Date creationDate) {
			this.creationDate = creationDate;
		}

		public Date getModDate() {
			return modDate;
		}

		public void setModDate(Date modDate) {
			this.modDate = modDate;
		}
		
		
	}